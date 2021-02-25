package com.sysdev.computation;

public class GraphConnectNode {
    private Coordinate coordinate;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GraphConnectNode(Coordinate coordinate, double distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "GraphConnectNode{" +
                "coordinate=" + coordinate +
                ", distance=" + distance +
                '}';
    }

    private double distance;
}
