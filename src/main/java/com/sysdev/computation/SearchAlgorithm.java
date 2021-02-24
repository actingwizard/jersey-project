package com.sysdev.computation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.*;

public class SearchAlgorithm {
    private int SEARCH_ALGO; // 0->dijkstra; 1->a*
    private HashMap<Coordinate, SearchNode> closedMap;
    private PriorityQueue<SearchNode> openPQ;
    private Coordinate START_NODE;
    private Coordinate END_NODE;
    private static final ObjectMapper mapper = new ObjectMapper();

    public SearchAlgorithm() {
        closedMap = new HashMap<>();
        openPQ = new PriorityQueue<>();
    }

    private double getHeuristic(Coordinate coordinate) {
        return SEARCH_ALGO == 0 ? 0 : Graph.calculateHaversine(coordinate, END_NODE);
    }

    private ArrayList<Coordinate> restorePath() {
        ArrayList<Coordinate> path = new ArrayList<>();
        System.out.println("Restoring the path:");
        SearchNode s = closedMap.get(END_NODE);
        while (s.getCoordinate() != START_NODE) {
            System.out.println(s);
            path.add(s.getCoordinate());
            s = closedMap.get(s.getPreviousCoordinate());
        }
        path.add(closedMap.get(START_NODE).getCoordinate());
        System.out.println(closedMap.get(START_NODE));
        return path;
//
//        String name = Main.class.getName().replace(".", File.separator);
//        String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + name.substring(0, name.lastIndexOf(File.separator));
//        JsonNode root = null;
//        try {
//            root = new ObjectMapper().readTree(new File(dir + "/mini.json"));
//            JsonNode features = root.path("features");
//            for (JsonNode jn : features) {
//                ObjectNode geometry = (ObjectNode) jn.path("geometry");
//                ArrayNode coordinates_node = mapper.createArrayNode();
//                for (Coordinate cr : path) {
//                    ArrayNode c = mapper.createArrayNode();
//                    c.add(cr.getLon());
//                    c.add(cr.getLat());
//                    coordinates_node.add(c);
//                }
//                geometry.set("coordinates", coordinates_node);
//            }
//
//            String resultUpdate = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
//            System.out.println("After Update " + resultUpdate);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }

    private boolean isNeedAdd(SearchNode successor) {
        if (closedMap.containsKey(successor.getCoordinate())) {
            if (closedMap.get(successor.getCoordinate()).getF() > successor.getF()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Coordinate> run(Coordinate startN, Coordinate endN, String search_method) {
        // since we have 2 methods, 0 -> dijkstra; 1 -> a*
        startN = Graph.getInstance().nextNode(startN);
        endN = Graph.getInstance().nextNode(endN);
        this.SEARCH_ALGO = search_method.equals("dijkstra") ? 0 : 1;
        this.START_NODE = startN;
        this.END_NODE = endN;
        SearchNode starting = new SearchNode(startN);
        starting.setG(0);
        openPQ.add(starting);

        while (!openPQ.isEmpty()) {
            System.out.println("NEW ITERATION");
            SearchNode[] events = openPQ.toArray(new SearchNode[openPQ.size()]);
            Arrays.sort(events, openPQ.comparator());
            System.out.print("openPQ:");
            for (SearchNode e : events) {
                System.out.print(e + " | ");
            }

            SearchNode q = openPQ.poll();
            ArrayList<Coordinate> neighbours = Graph.getInstance().nodeNeighbours(q.getCoordinate());
            System.out.println("\nCurr node:" + q);

            if (!closedMap.containsKey(q.getCoordinate()) || closedMap.get(q.getCoordinate()).getF() > q.getF()) {
                closedMap.put(q.getCoordinate(), q);
            }

            if (q.getCoordinate().equals(this.END_NODE)) {
                System.out.println("FOUND END");
                return restorePath();
            }
            for (Coordinate cr : neighbours) {
                SearchNode successor = new SearchNode(cr);
                successor.setG(q.getG() + Graph.getInstance().getDistance(q.getCoordinate(), cr));
                successor.setH(getHeuristic(cr));
                successor.calculateF();
                successor.setPreviousCoordinate(q.getCoordinate());
                System.out.println("S:" + successor);

                if (isNeedAdd(successor)) {
                    System.out.println("Added to PQ");
                    openPQ.add(successor);
                } else {
                    System.out.println("Not added to PQ");
                }
            }
        }
        return new ArrayList<>();
    }

}
