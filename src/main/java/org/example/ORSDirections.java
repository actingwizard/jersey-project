package org.example;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("orsdirections")
public class ORSDirections {

    @GET
    public Response getORSDirections(
            @DefaultValue("0.0") @QueryParam("originLat") double originLat,
            @DefaultValue("0.0") @QueryParam("originLon") double originLon,
            @DefaultValue("0.0") @QueryParam("destinationLat") double destinationLat,
            @DefaultValue("0.0") @QueryParam("destinationLon") double destinationLon) {

        JsonObject result = RequestRouteService.shortestPathSearch(originLon, originLat, destinationLon, destinationLat);
        System.out.println(result.toString());
        return Response
                .status(200)
                .entity(result).build();

    }
}
