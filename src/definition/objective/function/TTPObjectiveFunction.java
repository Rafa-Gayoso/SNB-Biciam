package definition.objective.function;

import definition.TTPDefinition;
import problem.definition.ObjetiveFunction;
import problem.definition.State;

import java.util.ArrayList;

public class TTPObjectiveFunction extends ObjetiveFunction {
    @Override
    public Double Evaluation(State state) {
        int penalizeVisitorGames = TTPDefinition.getInstance().penalizeVisitorGames(state);
        int penalizeHomeGames = TTPDefinition.getInstance().penalizeLocalGames(state);
        double totalDistance = 0;
        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(state);
        for (int i = 0; i < itinerary.size() - 1; i++) {
            ArrayList<Integer> row1 = itinerary.get(i);
            ArrayList<Integer> row2 = itinerary.get(i + 1);
            for (int j = 0; j < itinerary.get(i).size(); j++) {
                int first = row1.get(j);
                int second = row2.get(j);
                double dist = TTPDefinition.getInstance().getMatrixDistance()[second][first];
                totalDistance += dist;
            }
        }
        return totalDistance + (TTPDefinition.getInstance().getPenalization() * (penalizeHomeGames + penalizeVisitorGames));
    }




}
