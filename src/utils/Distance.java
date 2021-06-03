package utils;

import definition.TTPDefinition;
import definition.state.CalendarState;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Date;

public class Distance {

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
