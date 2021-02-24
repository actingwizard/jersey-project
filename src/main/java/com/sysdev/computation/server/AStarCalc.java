package com.sysdev.computation.server;

import com.sysdev.computation.Coordinate;
import com.sysdev.computation.SearchAlgorithm;
import com.sysdev.computation.SearchMethodAStar;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("astarcalc")
public class AStarCalc {
    @GET
    public Response getAStarCalc(
            @DefaultValue("0.0") @QueryParam("originLat") double originLat,
            @DefaultValue("0.0") @QueryParam("originLon") double originLon,
            @DefaultValue("0.0") @QueryParam("destinationLat") double destinationLat,
            @DefaultValue("0.0") @QueryParam("destinationLon") double destinationLon) {
        System.out.println("GET [AStar] from Routing Server:" +
                "oLat " + originLat +
                "; oLon " + originLon +
                "; dLat " + destinationLat +
                "; dLon " + destinationLon);
        SearchAlgorithm sa = new SearchAlgorithm();
        ArrayList<Coordinate> path = sa.run(new Coordinate(originLon, originLat), new Coordinate(destinationLon, destinationLat), new SearchMethodAStar());
//        System.out.println("Path in AStarCalc:" + path.toString());
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

        return Response
                .status(200)
                .entity(json_result).build();
    }
}
