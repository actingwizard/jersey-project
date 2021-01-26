package org.example;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orsdirections")
public class ORSDirections {

    @GET
    public Response getORSDirections(
            @DefaultValue("0.0") @QueryParam("originLat") double originLat,
            @DefaultValue("0.0") @QueryParam("originLon") double originLon,
            @DefaultValue("0.0") @QueryParam("destinationLat") double destinationLat,
            @DefaultValue("0.0") @QueryParam("destinationLon") double destinationLon) {
        System.out.println("GET from client:" +
                "oLat " + originLat +
                "; oLon " + originLon +
                "; dLat " + destinationLat +
                "; dLon " + destinationLon);
        JsonObject result = RequestRouteService.shortestPathSearch(originLon, originLat, destinationLon, destinationLat);
        System.out.println();
        return Response
                .status(200)
                .entity(result).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postORSDirections(PathCoordinates pc) {
        System.out.println("POST from client:" +
                "oLat " + pc.getOriginLat() +
                "; oLon " + pc.getOriginLon() +
                "; dLat " + pc.getDestinationLat() +
                "; dLon " + pc.getDestinationLon());
        JsonObject result = RequestRouteService.shortestPathSearch(pc.getOriginLon(), pc.getOriginLat(), pc.getDestinationLon(), pc.getDestinationLat());
        System.out.println();
        return Response
                .status(200)
                .entity(result).build();
    }

}
