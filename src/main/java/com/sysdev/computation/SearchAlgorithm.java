package com.sysdev.computation;

import java.util.*;

public class SearchAlgorithm {
    private HashMap<Coordinate, SearchNode> closedMap;
    private PriorityQueue<SearchNode> openPQ;
    private Coordinate START_NODE;
    private Coordinate END_NODE;

    public SearchAlgorithm() {
        closedMap = new HashMap<>();
        openPQ = new PriorityQueue<>();
    }

    private ArrayList<Coordinate> restorePath() {
        ArrayList<Coordinate> path = new ArrayList<>();
        System.out.println("Restoring the path..");
        SearchNode s = closedMap.get(END_NODE);
        while (s.getCoordinate() != START_NODE) {
            path.add(s.getCoordinate());
            s = closedMap.get(s.getPreviousCoordinate());
        }
        path.add(closedMap.get(START_NODE).getCoordinate());
        System.out.println("Path is restored");
        return path;
    }

    // returns true if node should be added to openList
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

    public ArrayList<Coordinate> run(Coordinate startN, Coordinate endN, SearchMethod searchMethod) {
        long startTime = System.nanoTime();
        startN = Graph.getInstance().nextNode(startN);
        endN = Graph.getInstance().nextNode(endN);
        this.START_NODE = startN;
        this.END_NODE = endN;
        SearchNode starting = new SearchNode(startN);
        starting.setG(0);
        openPQ.add(starting);
        System.out.println("Search started..");
        while (!openPQ.isEmpty()) {
            SearchNode q = openPQ.poll();
            ArrayList<Coordinate> neighbours = Graph.getInstance().nodeNeighbours(q.getCoordinate());
            if (!closedMap.containsKey(q.getCoordinate()) || closedMap.get(q.getCoordinate()).getF() > q.getF()) {
                closedMap.put(q.getCoordinate(), q);
            }
            if (q.getCoordinate().equals(this.END_NODE)) {
                System.out.println("Path is successfully found.");
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                System.out.println("Search took: " + duration + " ms");
                return restorePath();
            }
            for (Coordinate cr : neighbours) {
                SearchNode successor = new SearchNode(cr);
                successor.setG(q.getG() + Graph.getInstance().getDistance(q.getCoordinate(), cr));
                successor.setH(searchMethod.getHeuristic(cr, END_NODE));
                successor.calculateF();
                successor.setPreviousCoordinate(q.getCoordinate());
                if (isNeedAdd(successor)) {
                    openPQ.add(successor);
                }
            }
        }
        return new ArrayList<>();
    }

}
