package com.sysdev.computation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// Graph instance treated as SINGLETON
public class Graph {
    private static Graph INSTANCE = null;

    // internal field for graph storing
    private HashMap<Coordinate, ArrayList<GraphConnectNode>> database = null;

    public static Graph getInstance() {
        if (INSTANCE == null) {
            String filename = "schleswig-holstein.json";
            INSTANCE = new Graph(filename);
        }
        return INSTANCE;
    }

    private Graph(String fileName) {
        JsonNode tempArray = loadJSON(fileName);
        makeGraph(tempArray);
    }

    public double calculateHaversine(Coordinate cr1, Coordinate cr2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(cr2.getLat() - cr1.getLat());
        double dLon = Math.toRadians(cr2.getLon() - cr1.getLon());

        // convert to radians
        double lat1 = Math.toRadians(cr1.getLat());
        double lat2 = Math.toRadians(cr2.getLat());

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    // finds closest node from graph
    public Coordinate nextNode(Coordinate node) {
        double DIST_MIN = Double.MAX_VALUE;
        Coordinate closestNode = null;
        for (Coordinate cr : database.keySet()) {
            if (calculateHaversine(node, cr) < DIST_MIN) {
                closestNode = cr;
                DIST_MIN = calculateHaversine(node, cr);
            }
        }
        return closestNode;
    }

    // returns list of neighbours of Coordinate
    public ArrayList<Coordinate> nodeNeighbours(Coordinate node) {
        ArrayList<Coordinate> result = new ArrayList<>();
        ArrayList<GraphConnectNode> neighbours = database.get(node);
        for (GraphConnectNode gnc : neighbours) {
            result.add(gnc.getCoordinate());
        }
        return result;
    }

    // returns distance between 2 nodes in graph
    public double getDistance(Coordinate cr1, Coordinate cr2) {
        for (GraphConnectNode gnc : database.get(cr1)) {
            if (gnc.getCoordinate().equals(cr2)) {
                return gnc.getDistance();
            }
        }
        return 0.0;
    }

    private void makeGraph(JsonNode tempArray) {
        System.out.println("****\nInitializing Data Structures to store graph..");
        database = new HashMap<>();
        int MAX_DEG_VER = Integer.MIN_VALUE;
        for (JsonNode jn : tempArray) {
           if (jn.path("geometry").get("type").asText().equals("LineString")) {
                Coordinate prevCr = null;
                JsonNode crds = jn.path("geometry").path("coordinates");
                for (JsonNode coordinate : crds) {
                    Coordinate cr = new Coordinate(coordinate.get(0).asDouble(), coordinate.get(1).asDouble());
                    if (!database.containsKey(cr)) {
                        database.put(cr, new ArrayList<GraphConnectNode>());
                    }
                    if (prevCr == null) {
                        prevCr = cr;
                    } else {
                        double t_dist = calculateHaversine(cr, prevCr);
                        boolean shouldAdd = true;
                        for (GraphConnectNode gnc : database.get(cr)) {
                            if (gnc.getCoordinate().equals(prevCr)) {
                                shouldAdd = false;
                                break;
                            }
                        }
                        if (shouldAdd) {
                            database.get(cr).add(new GraphConnectNode(prevCr, t_dist));
                            database.get(prevCr).add(new GraphConnectNode(cr, t_dist));
                        }
                        if (database.get(cr).size() > MAX_DEG_VER)
                            MAX_DEG_VER = database.get(cr).size();
                        prevCr = cr;

                    }
                }
            }
        }
        System.out.println("Graph initialization is finished.");
        System.out.println("Graph STATS:\nNumber of nodes:" + database.size() + "\nMax degree of vertex:" + MAX_DEG_VER);
    }

    private JsonNode loadJSON(String filename) {
        System.out.println("****\nJSON Map of city is loading..");
        String name = Main.class.getName().replace(".", File.separator);
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + name.substring(0, name.lastIndexOf(File.separator));
        JsonNode tempArray = null;
        try {
            tempArray = new ObjectMapper().readTree(new File((path + "/" + filename))).get("features");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON Map of city successfully loaded.");
        return tempArray;
    }
}
