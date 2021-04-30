package utils;

import java.util.ArrayList;

public class AuxDuelDistanceAdded {

    private ArrayList<Integer> duel;
    private Double distance;

    public AuxDuelDistanceAdded(ArrayList<Integer> duel, Double distance) {
        this.duel = duel;
        this.distance = distance;
    }

    public ArrayList<Integer> getDuel() {
        return duel;
    }

    public void setDuel(ArrayList<Integer> duel) {
        this.duel = duel;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
