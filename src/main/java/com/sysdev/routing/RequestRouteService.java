package com.sysdev.routing;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

import java.io.StringReader;

public class RequestRouteService {
    private static final String OPENROUTESERVICE_URL = "https://api.openrouteservice.org/elevation/point";
    private static final String OPENROUTESERVICE_SHORTEST_PATH_URL = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";
    private static final String OPENROUTESERVICE_KEY = "5b3ce3597851110001cf62488ba2318e6f58480bb3f31a6533a3618e";

    public static JsonObject shortestPathSearch(double lon_s, double lat_s, double lon_f, double lat_f) {
        // create a json object which will be in the following format
        // {
        //      "coordinates": [[8.681495,49.41461],[8.687872,49.420318]]
        // }
        final JsonObject request = Json.createObjectBuilder()
                .add("coordinates",
                        Json.createArrayBuilder()
                                .add(
                                        Json.createArrayBuilder()
                                            .add(lon_s)
                                            .add(lat_s)
                                            .build())
                                .add(
                                        Json.createArrayBuilder()
                                            .add(lon_f)
                                            .add(lat_f)
                                            .build())
                                .build())
                .build();
        
        final JerseyClient client = new JerseyClientBuilder().build();
        final JerseyWebTarget webTarget = client.target(OPENROUTESERVICE_SHORTEST_PATH_URL);
        final Response response = webTarget
                .request()
                .header("Authorization", "5b3ce3597851110001cf62488ba2318e6f58480bb3f31a6533a3618e")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(Entity.json(request));

        System.out.println("Response status from ORService: " + response.getStatus());

        // check the result
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new RuntimeException("Failed: HTTP error code: " + response.getStatus());
        }

        // get the JSON response
        final String responseString = response.readEntity(String.class);
        final JsonObject jsonObject = Json.createReader(new StringReader(responseString)).readObject();

        // Extract the coordinates of the shortest path
        final JsonArray geometry = jsonObject
                .getJsonArray("features")
                .getJsonObject(0)
                .getJsonObject("geometry")
                .getJsonArray("coordinates");
        return jsonObject;
    }


    public static void poiSearch(double lat, double lon) {
        // create a json object which we will send in the post request
        // {
        //      format_in: "point",
        //      geometry: [lon, lat]
        // }

        final JsonObject request = Json.createObjectBuilder()
                .add("format_in", "point")
                .add(
                        "geometry",
                        Json.createArrayBuilder()
                                .add(lon)
                                .add(lat)
                                .build()
                )
                .build();
        System.out.println(request.toString());
        // use the jersey client api to make HTTP requests
        final JerseyClient client = new JerseyClientBuilder().build();
        final JerseyWebTarget webTarget = client.target(OPENROUTESERVICE_URL);
        final Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", OPENROUTESERVICE_KEY) // send the API key for authentication
                .post(Entity.json(request));
        System.out.println(response.toString());
        // check the result
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new RuntimeException("Failed: HTTP error code: " + response.getStatus());
        }

        // get the JSON response
        final String responseString = response.readEntity(String.class);
        final JsonObject jsonObject = Json.createReader(new StringReader(responseString)).readObject();
        System.out.println("Response: " + jsonObject);

        // Extract the elevation
        final JsonNumber elevation = jsonObject
                .getJsonObject("geometry")
                .getJsonArray("coordinates")
                .getJsonNumber(2);
        System.out.println("Elevation: " + elevation.doubleValue() + "m");
    }
}
