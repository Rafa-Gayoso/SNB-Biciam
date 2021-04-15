package operators.heuristics;


import java.util.ArrayList;
import java.util.Random;

import definition.TTPDefinition;
import problem.definition.State;
import definition.state.statecode.Date;
import utils.Distance;

public class DuelHeuristicOperator extends HeuristicOperator {


    private ArrayList<Integer> teams;
    private ArrayList<ArrayList<Integer>> duels;
    private State state;

    public DuelHeuristicOperator(State state, ArrayList<ArrayList<Integer>> duels){
        this.state = state;
        this.teams = TTPDefinition.getInstance().getTeamsIndexes();
        this.duels = duels;
    }

    public State generateCalendar(){
        ArrayList<Date> calendar = new ArrayList<>();
        boolean badSolution = true;

        while (badSolution){

            calendar = new ArrayList<>();

            ArrayList<ArrayList<Integer>> duelsCopy = new ArrayList<>();
            for (int i = 0; i < duels.size(); i++) {
                ArrayList<Integer> temp = new ArrayList<>();
                for (int j = 0; j < duels.get(i).size(); j++) {
                    temp.add(duels.get(i).get(j));
                }
                duelsCopy.add(temp);
            }

            Date date = new Date();
            Random random = new Random();
            int randomDuel = random.nextInt(duelsCopy.size());
            ArrayList<Integer> duel =  duelsCopy.get(randomDuel);
            date.getGames().add(duel);
            duelsCopy.remove(duel);
            fillFirstDate(date, duelsCopy);

            calendar.add(date);

            for(int i = 1; i < teams.size()-1;i++){
                Date newDate = new Date();
                int posDuel = -2;
                while(newDate.getGames().size() < date.getGames().size() && posDuel != -1){
                    posDuel =  posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);
                    if(posDuel != -1){
                        newDate.getGames().add(duelsCopy.get(posDuel));
                        duelsCopy.remove(duelsCopy.get(posDuel));
                    }
                /*else{
                    if (!tempList.isEmpty()){
                        posDuel = -2;
                        while (posDuel != -1){
                            posDuel = posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, tempList);
                            if(posDuel != -1){
                                newDate.getGames().add(tempList.get(posDuel));
                                tempList.remove(tempList.get(posDuel));
                            }
                        }
                    }
                    if (posDuel == -1){
                        tempList.add(newDate.getGames().get(0));
                        newDate.getGames().remove(tempList.get(tempList.size()-1));
                    }
                }*/
                }
                //duels.addAll(tempList);
                //tempList.clear();
                calendar.add(newDate);
            }

            badSolution = false;
            for (int i = 0; i < calendar.size() && !badSolution; i++) {
                if (calendar.get(i).getGames().size() != teams.size()/2){
                    badSolution = true;
                }
            }
        }

        state.getCode().addAll(calendar);
        return state;
    }

    private void fillFirstDate(Date date, ArrayList<ArrayList<Integer>> duelsCopy){

        Date dateToAdd = new Date();
        for (ArrayList<Integer> game: date.getGames()) {
            for (ArrayList<Integer> duel: duelsCopy) {
                if(!duel.contains(game.get(0)) && !duel.contains(game.get(1))){
                    boolean exists = false;
                    int i = 0;
                    while (i < dateToAdd.getGames().size() && !exists){
                        if (dateToAdd.getGames().get(i).contains(duel.get(0)) || dateToAdd.getGames().get(i).contains(duel.get(1))){
                            exists = true;

                        }
                        i++;
                    }
                    if (!exists){
                        if(dateToAdd.getGames().size() < (teams.size()/2)-1){
                            dateToAdd.getGames().add(duel);
                        }
                    }
                }
            }
        }
        date.getGames().addAll(dateToAdd.getGames());


        for(int i = 1; i < date.getGames().size(); i++){
            duelsCopy.remove(date.getGames().get(i));
        }
    }

    private int posLessDistanceDuel(Date date, Date newDate, ArrayList<ArrayList<Integer>> avalibleDuels){
        int pos = -1;
        double distance = Double.MAX_VALUE;
        for(ArrayList<Integer> gameNow: avalibleDuels){

            boolean exists = false;
            int i = 0;
            while (!exists && i < newDate.getGames().size()){

                if (newDate.getGames().get(i).contains(gameNow.get(0)) || newDate.getGames().get(i).contains(gameNow.get(1))){
                    exists = true;
                }
                i++;
            }

            if (!exists){
                double dist = 0;
                for (ArrayList<Integer> gameBefore: date.getGames()) {

                    int indexFirst = gameBefore.indexOf(gameNow.get(0));
                    int indexSecond = gameBefore.indexOf(gameNow.get(1));

                    if(indexFirst != -1){
                        dist += Distance.getInstance().getMatrixDistance()[gameBefore.get(0)][gameNow.get(0)];
                    }
                    if(indexSecond != -1){
                        dist += Distance.getInstance().getMatrixDistance()[gameBefore.get(0)][gameNow.get(0)];
                    }

                }
                if(dist < distance){
                    distance = dist;
                    pos = avalibleDuels.indexOf(gameNow);
                }
            }
        }
       return pos;
    }
}
