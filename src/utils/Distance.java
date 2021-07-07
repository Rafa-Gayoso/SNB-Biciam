package utils;

import definition.TTPDefinition;
import definition.state.CalendarState;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Date;

public class Distance implements IInauguralGame, ISecondRound {

    private static Distance distanceInstance;

    private double[][] matrixDistance;//Matrix that represents the distance between resources.teams

    private Distance(){
        fillMatrixDistance();

    }
    public static Distance getInstance(){
        if (distanceInstance == null){
            distanceInstance = new Distance();
        }
        return distanceInstance;
    }

    public float calculateCalendarDistance(State state)  {
        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(state);
        float totalDistance = 0;

        for (int i = 0; i < itinerary.size() - 1; i++) {
            ArrayList<Integer> row1 = itinerary.get(i);
            ArrayList<Integer> row2 = itinerary.get(i + 1);
            for (int j = 0; j < itinerary.get(i).size(); j++) {
                int first = row1.get(j);
                int second = row2.get(j);
                double dist = matrixDistance[second][first];
                totalDistance += dist;
            }
        }
        return totalDistance;
    }

    public double calculateCalendarDistanceConvergence(State calendar)  {
        State state = calendar.clone();
        CalendarConfiguration configuration = ((CalendarState)state).getConfiguration();
        int penalizeVisitorGames = TTPDefinition.getInstance().penalizeVisitorGames(state);
        int penalizeHomeGames = TTPDefinition.getInstance().penalizeLocalGames(state);
        int penalizeChampion = 0;
        int penalizeInauguralGame = 0;

        if(configuration.isChampionVsSecondPlace()){
            if(!configuration.isInauguralGame())
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

    public void fillMatrixDistance() {

        DataFiles.getSingletonDataFiles().createTeamsPairDistances();
        ArrayList<TeamsPairDistance> teamsPairDistances = DataFiles.getSingletonDataFiles().getTeamsPairDistances();
        this.matrixDistance = new double[DataFiles.getSingletonDataFiles().getTeams().size()][DataFiles.getSingletonDataFiles().getTeams().size()];
        for (TeamsPairDistance aux : teamsPairDistances) {
            int indexTeam1 = aux.getFirstTeam();
            int indexTeam2 = aux.getSecondTeam();
            double distance = aux.getDistance();
            matrixDistance[indexTeam1][indexTeam2] = distance;
            matrixDistance[indexTeam2][indexTeam1] = distance;
        }


    }

    public ArrayList<ArrayList<Double>> itineraryDistances(State state) {
        ArrayList<ArrayList<Double>> distancesItinerary = new ArrayList<>();
        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(state);
        CalendarConfiguration configuration = ((CalendarState)state).getConfiguration();
        for (int i = 0; i < itinerary.size() - 1; i++) {

            ArrayList<Double> distances = new ArrayList<>(configuration.getTeamsIndexes().size());

            for (int m = 0; m < configuration.getTeamsIndexes().size(); m++) {
                distances.add(0.0);
            }

            ArrayList<Integer> row1 = itinerary.get(i);
            ArrayList<Integer> row2 = itinerary.get(i + 1);
            for (int j = 0; j < itinerary.get(i).size(); j++) {
                int first = row1.get(j);
                int second = row2.get(j);
                double dist = matrixDistance[second][first];
                distances.set(j, distances.get(j) + dist);
                if (configuration.getTeamsIndexes().get(j) == second) {
                    distances.set(j, 0.0);
                }
            }
            distancesItinerary.add(distances);
        }
        return distancesItinerary;
    }

    public double[][] getMatrixDistance() {
        return matrixDistance;
    }

    public void setMatrixDistance(double[][] matrixDistance) {
        this.matrixDistance = matrixDistance;
    }

}
