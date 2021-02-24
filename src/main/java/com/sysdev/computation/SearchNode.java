package com.sysdev.computation;

import java.util.Objects;

public class SearchNode implements Comparable<SearchNode> {
    private Coordinate coordinate;
    private Coordinate previousCoordinate;
    private double f;
    private double g;
    private double h;

    @Override
    public String toString() {
        return "SearchNode{" +
                "coordinate=" + coordinate +
                ", previousCoordinate=" + previousCoordinate +
                ", f=" + f +
                ", g=" + g +
                ", h=" + h +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchNode that = (SearchNode) o;
        return coordinate.equals(that.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }

    public Coordinate getPreviousCoordinate() {
        return previousCoordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public double getF() {
        return f;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public SearchNode(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.f = 0;
    }

    public void calculateF() {
        this.f = this.g + this.h;
    }

    public void setPreviousCoordinate(Coordinate previousCoordinate) {
        this.previousCoordinate = previousCoordinate;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }

    @Override
    public int compareTo(SearchNode o) {
        return Double.compare(this.getF(), o.getF());
    }
}
