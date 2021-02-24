package com.sysdev.computation;

public class SearchMethodAStar implements SearchMethod{
    @Override
    public double getHeuristic(Coordinate c, Coordinate END_NODE) {
        return Graph.getInstance().calculateHaversine(c, END_NODE);
    }
}
