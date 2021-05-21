package definition.objective.function;

import definition.TTPDefinition;
import definition.state.CalendarState;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import problem.definition.ObjetiveFunction;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;

public class TTPObjectiveFunction extends ObjetiveFunction implements ISecondRound, IInauguralGame {
    @Override
    public Double Evaluation(State calendar) {
        State state = calendar.clone();
        CalendarConfiguration configuration = ((CalendarState)state).getConfiguration();
        int penalizeVisitorGames = TTPDefinition.getInstance().penalizeVisitorGames(state);
        int penalizeHomeGames = TTPDefinition.getInstance().penalizeLocalGames(state);
        int penalizeChampion = 0;
        int penalizeInauguralGame = 0;

        if(configuration.isChampionVsSecondPlace()){
            if(configuration.isInauguralGame())
                penalizeChampion = TTPDefinition.getInstance().penalizeChampionGame(state);
            else
                penalizeInauguralGame = TTPDefinition.getInstance().penalizeInauguralGame(state);
        }

        if(configuration.isSymmetricSecondRound()){
            if(configuration.isInauguralGame()){
                deleteInauguralGame(state);
            }
            setSecondRound(state);
            if(configuration.isInauguralGame()){
                addInauguralGame(state);
            }

        }

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

        /*f(configuration.isSymmetricSecondRound()){
           deleteSecondRound(state);
           if(configuration.isInauguralGame()){
               addInauguralGame(state);
           }
        }*/

        return totalDistance + (TTPDefinition.getInstance().getPenalization() * (penalizeHomeGames + penalizeVisitorGames
        + penalizeChampion + penalizeInauguralGame));
    }




}
