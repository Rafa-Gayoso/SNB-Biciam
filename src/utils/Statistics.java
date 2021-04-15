package utils;

import java.util.ArrayList;

public class Statistics {

    private String team;
    private double distance;
    private ArrayList<String> teams;
    private static Statistics statisticsInstance;



    private Statistics(){
        this.team = "";
        this.distance= 0.0;
        this.teams = DataFiles.getSingletonDataFiles().getTeams();
    }

    public static Statistics getInstance(){
        if(statisticsInstance ==  null){
            statisticsInstance = new Statistics();
        }
        return statisticsInstance;
    }

    public void lessStatistics(ArrayList<ArrayList<Double>> itineraryDistances) {


        double min = Double.MAX_VALUE;
        double sum = 0;
        int pos = -1;
        for (int l = 0; l < itineraryDistances.get(0).size(); l++) {

            for (ArrayList<Double> itineraryDistance : itineraryDistances) {
                sum += itineraryDistance.get(l);
            }

            if (sum <= min) {
                min = sum;
                pos = l;
            }
            sum = 0;
        }

        team = teams.get(pos);
        distance = min;
        //return new CalendarStatistic(teams.get(pos),(float) max);

    }



    public void moreStatistics(ArrayList<ArrayList<Double>> itineraryDistances) {


        double max = Double.MIN_VALUE;
        double sum = 0;
        int pos = -1;
        for (int l = 0; l < itineraryDistances.get(0).size(); l++) {

            for (int p = 0; p < itineraryDistances.size(); p++) {
                sum += itineraryDistances.get(p).get(l);
            }

            if (sum >= max) {
                max = sum;
                pos = l;
            }
            sum = 0;
        }


        team = teams.get(pos);
        distance = max;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


}
