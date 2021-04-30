package operators.heuristics;

import definition.TTPDefinition;
import utils.AuxPosDateAndPace;
import utils.Distance;

import java.util.ArrayList;
import java.util.Random;
import problem.definition.State;
import definition.state.statecode.Date;

public class DateHeuristicOperator extends HeuristicOperator{


    private ArrayList<Integer> teams;
    private ArrayList<ArrayList<Integer>> duels;
    private ArrayList<Date> dateList;
    private DuelHeuristicOperator duelHeuristic;

    public void initializeDateHeuristicOperator(ArrayList<ArrayList<Integer>> duels){
        this.teams = TTPDefinition.getInstance().getTeamsIndexes();
        this.duels = duels;
        this.dateList = new ArrayList<>();
        this.duelHeuristic = new DuelHeuristicOperator();
        this.dateList = duelHeuristic.generateCalendar(duels);
    }


    public ArrayList<Date> generateCalendar(ArrayList<ArrayList<Integer>> duels){
        initializeDateHeuristicOperator(duels);

        ArrayList<Date> calendar = new ArrayList<>();
        int cantDates = dateList.size();

        Random random = new Random();
        int randomDate = random.nextInt(dateList.size());
        System.out.println("Fecha inicial: " + randomDate);
        calendar.add(dateList.get(randomDate));
        dateList.remove(dateList.get(randomDate));


        for(int i = 0; i < cantDates;i++){

            while(calendar.size() < cantDates){
                State state = new State();
                state.getCode().addAll(dateList);
                AuxPosDateAndPace posDatePlace =  posLessDistanceDate(calendar, state);
                if(posDatePlace.getPosDate() != -1){
                    calendar.add(posDatePlace.getPosPlace(), dateList.get(posDatePlace.getPosDate()));
                    dateList.remove(dateList.get(posDatePlace.getPosDate()));
                }
            }
        }

        return calendar;
    }

    private AuxPosDateAndPace posLessDistanceDate(ArrayList<Date> calendar, State state){
        int posDate = -1;
        int posPlace = -1;
        double distance = Double.MAX_VALUE;

        for (int i = 0; i < state.getCode().size(); i++) {
            Date dateAnalized = (Date) state.getCode().get(i);

            for (int j = 0; j < calendar.size()+1; j++) {

                calendar.add(j,dateAnalized);
                //double newDistance = calculateDistance(teamsItinerary(calendar));
                double newDistance = Distance.getInstance().calculateCalendarDistance(state);

                if (newDistance < distance){
                    posPlace = j;
                    posDate = i;
                    distance = newDistance;
                }
                calendar.remove(j);
            }
        }

        AuxPosDateAndPace result = new AuxPosDateAndPace(posDate, posPlace);

        return result;
    }
}
