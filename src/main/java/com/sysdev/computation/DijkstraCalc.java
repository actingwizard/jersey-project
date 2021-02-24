package com.sysdev.computation;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("dijkstracalc")
public class DijkstraCalc {
    @GET
    public Response getDijkstraCalc(
            @DefaultValue("0.0") @QueryParam("originLat") double originLat,
            @DefaultValue("0.0") @QueryParam("originLon") double originLon,
            @DefaultValue("0.0") @QueryParam("destinationLat") double destinationLat,
            @DefaultValue("0.0") @QueryParam("destinationLon") double destinationLon) {
        System.out.println("GET from client:" +
                "oLat " + originLat +
                "; oLon " + originLon +
                "; dLat " + destinationLat +
                "; dLon " + destinationLon);
//        Graph.shortestPath(new Coordinate(9.826841354370117,54.483552499291534), new Coordinate(9.839372634887695,54.4656480544963), "dijkstra");
        SearchAlgorithm sa = new SearchAlgorithm();
        ArrayList<Coordinate> path = sa.run(new Coordinate(originLon, originLat), new Coordinate(destinationLon, destinationLat), "dijkstra");
        System.out.println("Path in DijkstraCalc:" + path.toString());
        JsonArrayBuilder coordinates_builder = Json.createArrayBuilder();
        for (Coordinate c : path) {
            coordinates_builder.add(Json.createArrayBuilder().add(c.getLon()).add(c.getLat()).build());
        }

        final JsonObject json_result = Json.createObjectBuilder()
                .add("features", Json.createArrayBuilder()
                                    .add(Json.createObjectBuilder()
                                            .add("type", "Feature")
                                            .add("properties", Json.createObjectBuilder().build())
                                            .add("geometry", Json.createObjectBuilder()
                                                                .add("type", "LineString")
                                                                .add("coordinates", coordinates_builder.build())
                                                                .build())
                                            ))
                .add("type", "FeatureCollection")
                .build();

        System.out.println(json_result.toString());
        return Response
                .status(200)
                .entity(json_result).build();
    }
}
