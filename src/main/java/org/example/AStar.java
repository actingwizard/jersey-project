package org.example;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

import java.io.StringReader;

@Path("/astar")
public class AStar {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAStarPath(
            @DefaultValue("0.0") @QueryParam("originLat") double originLat,
            @DefaultValue("0.0") @QueryParam("originLon") double originLon,
            @DefaultValue("0.0") @QueryParam("destinationLat") double destinationLat,
            @DefaultValue("0.0") @QueryParam("destinationLon") double destinationLon) {
        System.out.println("GET AStar from client:" +
                "oLat " + originLat +
                "; oLon " + originLon +
                "; dLat " + destinationLat +
                "; dLon " + destinationLon);

        final JerseyClient client = new JerseyClientBuilder().build();
        final JerseyWebTarget webTarget = client.target("http://localhost:8080/myapp/astarcalc")
                .queryParam("originLat", originLat)
                .queryParam("originLon", originLon)
                .queryParam("destinationLat", destinationLat)
                .queryParam("destinationLon", destinationLon);

        final Response response = webTarget
                .request()
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .get(Response.class);

        System.out.println("Response status from ORService: " + response.getStatus());

        // check the result
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new RuntimeException("Failed: HTTP error code: " + response.getStatus());
        }

        // get the JSON response
        final String responseString = response.readEntity(String.class);
        final JsonObject jsonObject = Json.createReader(new StringReader(responseString)).readObject();

        return Response
                .status(200)
                .entity(jsonObject).build();
    }
}
