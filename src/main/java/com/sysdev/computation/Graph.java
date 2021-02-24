package com.sysdev.computation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    private static Graph INSTANCE = null;

    private ArrayList<Coordinate> coordinates;
    private double[][] adjMat = null;
    private HashMap<Coordinate, Integer> labelsMap = null;

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

    public Coordinate nextNode(Coordinate node) {
        double DIST_MIN = Double.MAX_VALUE;
        Coordinate closestNode = null;
        for (int i = 0; i < coordinates.size(); ++i) {
            if (calculateHaversine(node, coordinates.get(i)) < DIST_MIN) {
                closestNode = coordinates.get(i);
                DIST_MIN = calculateHaversine(node, coordinates.get(i));
            }
        }
        return closestNode;
    }

//    public void shortestPath(Coordinate startN, Coordinate endN, String searchMethod) {
//        SearchAlgorithm sa = new SearchAlgorithm();
//        sa.run(nextNode(startN), nextNode(endN), searchMethod);
//    }

    public ArrayList<Coordinate> nodeNeighbours(Coordinate node) {
        ArrayList<Coordinate> result = new ArrayList<>();
        for (int i = 0; i < adjMat.length; ++i) {
            if (adjMat[getLabel(node)][i] != 0) {
                result.add(getCoordinate(i));
            }
        }
        return result;
    }

    private void makeGraph(JsonNode tempArray) {
        coordinates = new ArrayList<>();
        for (JsonNode jn : tempArray) {
            if (jn.path("geometry").get("type").asText().equals("LineString")) {
                if (labelsMap == null) {
                    labelsMap = new HashMap<Coordinate, Integer>();
                }
                JsonNode crds = jn.path("geometry").path("coordinates");
                for (JsonNode coordinate : crds) {
                    Coordinate cr = new Coordinate(coordinate.get(0).asDouble(), coordinate.get(1).asDouble());
                    if (!labelsMap.containsKey(cr)) {
                        coordinates.add(cr);
                        labelsMap.put(cr, coordinates.size() - 1);
                    }
                }
            }
        }

        System.out.println("Number of nodes:" + coordinates.size());
        for (int i = 0; i < coordinates.size(); ++i)
            System.out.println(i + ":" + coordinates.get(i).toString());

        adjMat = new double[coordinates.size()][coordinates.size()];
        for (int i = 0; i < adjMat.length; ++i) {
            for (int j = 0; j < adjMat.length; ++j) {
                adjMat[i][j] = 0;
            }
        }


        for (JsonNode jn : tempArray) {
            JsonNode geometry = jn.path("geometry");
            if (jn.path("geometry").get("type").asText().equals("LineString")) {
                Coordinate prevCr = null;
                JsonNode crds = jn.path("geometry").path("coordinates");
                for (JsonNode coordinate : crds) {
                    Coordinate cr = new Coordinate(coordinate.get(0).asDouble(), coordinate.get(1).asDouble());
                    if (prevCr == null) {
                        prevCr = cr;
                    } else {
                        double t_dist = calculateHaversine(cr, prevCr);
                        adjMat[getLabel(cr)][getLabel(prevCr)] = t_dist;
                        adjMat[getLabel(prevCr)][getLabel(cr)] = t_dist;
                        prevCr = cr;
                    }
                }
            }
        }
    }

    private JsonNode loadJSON(String filename) {
        String name = Main.class.getName().replace(".", File.separator);
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + name.substring(0, name.lastIndexOf(File.separator));
        JsonNode tempArray = null;
        try {
            tempArray = new ObjectMapper().readTree(new File((path + "/" + filename))).get("features");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempArray;
    }

    public static double calculateHaversine(Coordinate cr1, Coordinate cr2) {
//        return Math.sqrt(Math.pow(cr2.getLat() - cr1.getLat(), 2.0) + Math.pow(cr2.getLon() - cr1.getLon(), 2.0));
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

    private int getLabel(Coordinate cr) {
        return labelsMap.get(cr);
    }

    private Coordinate getCoordinate(int label) {
        return coordinates.get(label);
    }

    public double getDistance(Coordinate cr1, Coordinate cr2) {
        return adjMat[getLabel(cr1)][getLabel(cr2)];
    }

}
