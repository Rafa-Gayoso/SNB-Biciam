package utils;

import problem.definition.State;

import java.util.ArrayList;

public class AuxStatePlusIterations {

    private State state;
    private ArrayList<Float> distItarations;
    private int metaheuristic;

    public AuxStatePlusIterations(State state, int metaheuristic) {
        this.state = state;
        this.distItarations = new ArrayList<>();
        this.metaheuristic = metaheuristic;
    }



    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ArrayList<Float> getDistItarations() {
        return distItarations;
    }

    public void setDistItarations(ArrayList<Float> distItarations) {
        this.distItarations = distItarations;
    }

    public int getMetaheuristic() {
        return metaheuristic;
    }

    public void setMetaheuristic(int metaheuristic) {
        this.metaheuristic = metaheuristic;
    }
}
