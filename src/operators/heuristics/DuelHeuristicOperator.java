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

    public void initializeDuelHeuristicOperator(ArrayList<ArrayList<Integer>> duels){
        this.teams = TTPDefinition.getInstance().getTeamsIndexes();
        this.duels = duels;
    }

    /*public ArrayList<Date> generateCalendarOld(ArrayList<ArrayList<Integer>> duels){
        initializeDuelHeuristicOperator(duels);

        ArrayList<Date> calendar = new ArrayList<>();
        boolean badSolution = true;
        int x = 0;
        while (badSolution){
            System.out.println(x);
            x++;
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
            ArrayList<ArrayList<Integer>> tempList = new ArrayList<>();

            for(int i = 1; i < teams.size()-1;i++){
                Date newDate = new Date();
                int posDuel = -2;
                while(newDate.getGames().size() < date.getGames().size()){
                    posDuel =  posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);
                    if(posDuel != -1){
                        newDate.getGames().add(duelsCopy.get(posDuel));
                        duelsCopy.remove(duelsCopy.get(posDuel));
                    }
                    else{
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
                        if (newDate.getGames().size() < date.getGames().size()){
                            tempList.addAll(newDate.getGames());
                            newDate.getGames().clear();
                        }



                        if (!tempList.isEmpty()){
                            posDuel = -2;
                            while (posDuel != -1){
                                posDuel = posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, tempList);
                                if(posDuel != -1){
                                    newDate.getGames().add(tempList.get(posDuel));
                                    tempList.remove(tempList.get(posDuel));

                                    posDuel =  posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);
                                    if(posDuel != -1){
                                        newDate.getGames().add(duelsCopy.get(posDuel));
                                        duelsCopy.remove(duelsCopy.get(posDuel));
                                    }
                                }
                            }
                        }
                        if (posDuel == -1){
                            tempList.add(newDate.getGames().get(newDate.getGames().size()-1));
                            newDate.getGames().remove(tempList.get(tempList.size()-1));
                        }
                    }
                    if(duelsCopy.isEmpty() && newDate.getGames().isEmpty()){
                        duelsCopy.addAll(tempList);
                        duelsCopy.addAll(newDate.getGames());

                        for (int j = 0; j < calendar.get(calendar.size()-1).getGames().size()-1; j++) {
                            newDate.getGames().add(calendar.get(calendar.size()-1).getGames().get(j));
                        }
                        tempList.clear();
                        tempList.add(calendar.get(calendar.size()-1).getGames().get(calendar.get(calendar.size()-1).getGames().size()-1));
                        calendar.remove(calendar.size()-1);
                    }
                }
                duelsCopy.addAll(tempList);
                tempList.clear();
                calendar.add(newDate);
            }

            badSolution = false;
            for (int i = 0; i < calendar.size() && !badSolution; i++) {
                if (calendar.get(i).getGames().size() != teams.size()/2){
                    badSolution = true;
                }
            }
        }
        System.out.println("///////////////////////////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////////////////////////");
        System.out.println("///////////////////////////////////////////////////////////////////////////");
        return calendar;
    }*/

    public ArrayList<Date> generateCalendar(ArrayList<ArrayList<Integer>> duels){
        initializeDuelHeuristicOperator(duels);

        ArrayList<Date> calendar = new ArrayList<>();

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

        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> calendarBacktracking = new ArrayList<>();

        for (int i = 1; i < teams.size()-1; i++) {
            ArrayList<ArrayList<ArrayList<Integer>>> dateBacktracking = new ArrayList<>();
            for (int j = 0; j < teams.size()/2; j++) {
                ArrayList<ArrayList<Integer>> duelBacktracking = new ArrayList<>();
                dateBacktracking.add(duelBacktracking);
            }
            calendarBacktracking.add(dateBacktracking);
        }


        for (int i = 0; i < calendarBacktracking.size(); i++) {
            System.out.println("Ciclo infinito 1");
            Date newDate = new Date();
            int j = 0;
            for (; j < calendarBacktracking.get(i).size(); j++) {
                System.out.println("Ciclo infinito 2");
                calendarBacktracking.get(i).get(j).clear();
                int pos = -2;
                while (pos != -1){
                    System.out.println("Ciclo infinito 3");
                    pos = posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);

                    if(pos != -1){
                        calendarBacktracking.get(i).get(j).add(duelsCopy.get(pos));
                        duelsCopy.remove(duelsCopy.get(pos));
                    }
                }
                if (!calendarBacktracking.get(i).get(j).isEmpty()){
                    newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                    duelsCopy.addAll(calendarBacktracking.get(i).get(j));
                }
                else{
                    j = newDate.getGames().size()-1;
                    calendarBacktracking.get(i).get(j).remove(0);
                    newDate.getGames().remove(newDate.getGames().size()-1);
                    if (!calendarBacktracking.get(i).get(j).isEmpty()){
                        newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                    }
                    else{
                        j = newDate.getGames().size()-1;
                        boolean stop = false;
                        while (i >= 0 && !stop){
                            System.out.println("Ciclo infinito 4");
                            while (j >= 0 && !stop){
                                System.out.println("Ciclo infinito 5");
                                calendarBacktracking.get(i).get(j).remove(0);
                                newDate.getGames().remove(newDate.getGames().size()-1);
                                if (!calendarBacktracking.get(i).get(j).isEmpty()){
                                    newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                                    stop = true;
                                    j++;
                                }
                                j--;
                            }
                            if (j == -1 && !stop){
                                i--;
                                j = calendarBacktracking.get(i).size()-1;
                                newDate.getGames().clear();
                                newDate.getGames().addAll(calendar.get(calendar.size()-1).getGames());
                                duelsCopy.addAll(calendar.get(calendar.size()-1).getGames());
                                calendar.remove(calendar.size()-1);
                            }
                        }
                    }
                }
            }

            calendar.add(newDate);
            duelsCopy.removeAll(newDate.getGames());

            /*if (newDate.getGames().size() != teams.size()/2){
                int posBack = newDate.getGames().indexOf(newDate.getGames().get(newDate.getGames().size()-1));
                calendarBacktracking.get(i).get(posBack).remove(0);
                newDate.getGames().remove(posBack);
                newDate.getGames().add(calendarBacktracking.get(i).get(posBack).get(0));
                j = posBack;
                i--;
            }
            else{
                calendar.add(newDate);
            }*/
        }



        /*ArrayList<ArrayList<Integer>> tempList = new ArrayList<>();
        for(int i = 1; i < teams.size()-1;i++){
            Date newDate = new Date();
            int posDuel = -2;
            while(newDate.getGames().size() < date.getGames().size()){
                posDuel =  posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);
                if(posDuel != -1){
                    newDate.getGames().add(duelsCopy.get(posDuel));
                    duelsCopy.remove(duelsCopy.get(posDuel));
                }
                else{
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
                    if (newDate.getGames().size() < date.getGames().size()){
                        tempList.addAll(newDate.getGames());
                        newDate.getGames().clear();
                    }

                       if (!tempList.isEmpty()){
                           posDuel = -2;
                           while (posDuel != -1){
                               posDuel = posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, tempList);
                               if(posDuel != -1){
                                   newDate.getGames().add(tempList.get(posDuel));
                                   tempList.remove(tempList.get(posDuel));
                                   posDuel =  posLessDistanceDuel(calendar.get(calendar.size()-1), newDate, duelsCopy);
                                   if(posDuel != -1){
                                       newDate.getGames().add(duelsCopy.get(posDuel));
                                       duelsCopy.remove(duelsCopy.get(posDuel));
                                   }
                               }
                           }
                       }
                    if (posDuel == -1){
                           tempList.add(newDate.getGames().get(newDate.getGames().size()-1));
                           newDate.getGames().remove(tempList.get(tempList.size()-1));
                       }
                }
                if(duelsCopy.isEmpty() && newDate.getGames().isEmpty()){
                    duelsCopy.addAll(tempList);
                    duelsCopy.addAll(newDate.getGames());
                    for (int j = 0; j < calendar.get(calendar.size()-1).getGames().size()-1; j++) {
                        newDate.getGames().add(calendar.get(calendar.size()-1).getGames().get(j));
                    }
                    tempList.clear();
                    tempList.add(calendar.get(calendar.size()-1).getGames().get(calendar.get(calendar.size()-1).getGames().size()-1));
                    calendar.remove(calendar.size()-1);
                }
            }
            duelsCopy.addAll(tempList);
            tempList.clear();
            calendar.add(newDate);
        }*/

        return calendar;
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
