package operators.heuristics;


import java.util.*;

import definition.TTPDefinition;
import problem.definition.State;
import definition.state.statecode.Date;
import utils.AuxDuelDistanceAdded;
import utils.Distance;

public class DuelHeuristicOperator extends HeuristicOperator {


    private ArrayList<Integer> teams;
    private ArrayList<ArrayList<Integer>> duels;
    private boolean series32;

    public void initializeDuelHeuristicOperator(ArrayList<ArrayList<Integer>> duels){

        //DEBUG
        System.out.println("DuelHeuristicOperator.initializeDuelHeuristicOperator()");

        this.teams = (ArrayList<Integer>) TTPDefinition.getInstance().getTeamsIndexes().clone();
        this.duels = duels;
    }

    /*public ArrayList<Date> generateCalendarSlow(ArrayList<ArrayList<Integer>> duels){
        System.out.println("Empieza///////////////////////////////////////");
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
        fillFirstDateSlow(date, duelsCopy);
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

            Date newDate = new Date();
            int j = 0;
            for (; j < calendarBacktracking.get(i).size(); j++) {

                calendarBacktracking.get(i).get(j).clear();
                int pos = -2;
                while (pos != -1){

                    pos = posLessDistanceDuel(calendar, calendar.get(calendar.size()-1), newDate, duelsCopy);

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
                            System.out.println("i = "+i);
                            while (j >= 0 && !stop){
                                System.out.println("j = "+j);
                                calendarBacktracking.get(i).get(j).remove(0);
                                newDate.getGames().remove(newDate.getGames().size()-1);
                                if (!calendarBacktracking.get(i).get(j).isEmpty()){
                                    newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                                    stop = true;
                                }
                                j--;
                            }
                            if (j == -1 && !stop){
                                i--;
                                j = calendarBacktracking.get(i).size()-1;

                                newDate.getGames().clear();
                                newDate.getGames().addAll(calendar.get(calendar.size()-1).getGames());
                                calendar.remove(calendar.size()-1);
                            }
                            if (stop){
                                j++;
                            }
                        }
                    }
                }
            }

            calendar.add(newDate);
        }

        System.out.println("Termina///////////////////////////////////////");
        return calendar;
    }*/

    public ArrayList<Date> generateCalendar(ArrayList<ArrayList<Integer>> duels){

        //DEBUG
        System.out.println("DuelHeuristicOperator.generateCalendar()");

        /*series32 = true;
        if(series32) { //Si no es un calendario del tipo series de 3 y 2 juegos, entonces:
            ArrayList<ArrayList<Integer>> clone = (ArrayList<ArrayList<Integer>>) duels.clone();
            duels.addAll(clone);
        }*/
            initializeDuelHeuristicOperator(duels);

            Set<ArrayList<Integer>> setDuelsCalendar = new HashSet<>();
            Set<Integer> setTeamsDate = new HashSet<>();

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
            ArrayList<Integer> duel = duelsCopy.get(randomDuel);
            date.getGames().add(duel);
            duelsCopy.remove(duel);
            setDuelsCalendar.add(duel);
            fillFirstDateFast(date, duelsCopy, setDuelsCalendar);
            calendar.add(date);


            ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> calendarBacktracking = new ArrayList<>();
            for (int i = 1; i < TTPDefinition.getInstance().getNumberOfDates(); i++) {
                ArrayList<ArrayList<ArrayList<Integer>>> dateBacktracking = new ArrayList<>();
                for (int j = 0; j < teams.size() / 2; j++) {
                    ArrayList<ArrayList<Integer>> duelBacktracking = new ArrayList<>();
                    dateBacktracking.add(duelBacktracking);
                }
                calendarBacktracking.add(dateBacktracking);
            }

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < calendarBacktracking.size(); i++) {

                Date newDate = new Date();
                setTeamsDate = new HashSet<>();
                int j = 0;
                for (; j < calendarBacktracking.get(i).size(); j++) {

                    if (System.currentTimeMillis() - startTime > 350) {
                        break;
                    }
                    calendarBacktracking.get(i).get(j).clear();

                    ArrayList<ArrayList<Integer>> orderedPosibleDuels = lessDistanceDuels(setDuelsCalendar, setTeamsDate, calendar.get(calendar.size() - 1), duelsCopy);
                    calendarBacktracking.get(i).get(j).addAll(orderedPosibleDuels);

                    if (!calendarBacktracking.get(i).get(j).isEmpty()) {
                        newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                        setTeamsDate.addAll(calendarBacktracking.get(i).get(j).get(0));
                    } else {
                        j = newDate.getGames().size() - 1;
                        calendarBacktracking.get(i).get(j).remove(0);
                        setTeamsDate.removeAll(newDate.getGames().get(newDate.getGames().size() - 1));
                        newDate.getGames().remove(newDate.getGames().size() - 1);

                        if (!calendarBacktracking.get(i).get(j).isEmpty()) {
                            newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                            setTeamsDate.addAll(calendarBacktracking.get(i).get(j).get(0));
                        } else {
                            j = newDate.getGames().size() - 1;
                            boolean stop = false;

                            while (i >= 0 && !stop) {
                                while (j >= 0 && !stop) {
                                    calendarBacktracking.get(i).get(j).remove(0);
                                    setTeamsDate.removeAll(newDate.getGames().get(newDate.getGames().size() - 1));
                                    newDate.getGames().remove(newDate.getGames().size() - 1);
                                    if (!calendarBacktracking.get(i).get(j).isEmpty()) {
                                        newDate.getGames().add(calendarBacktracking.get(i).get(j).get(0));
                                        setTeamsDate.addAll(calendarBacktracking.get(i).get(j).get(0));
                                        stop = true;
                                    }
                                    j--;
                                }
                                if (j == -1 && !stop) {
                                    i--;
                                    j = calendarBacktracking.get(i).size() - 1;

                                    newDate.getGames().clear();
                                    setTeamsDate.clear();
                                    for (ArrayList<Integer> temp : calendar.get(calendar.size() - 1).getGames()) {
                                        newDate.getGames().add(temp);
                                        setTeamsDate.addAll(temp);
                                    }

                                    setDuelsCalendar.removeAll(calendar.get(calendar.size() - 1).getGames());
                                    calendar.remove(calendar.size() - 1);
                                }
                                if (stop) {
                                    j++;
                                }
                            }
                        }
                    }
                }
                if (System.currentTimeMillis() - startTime > 350) {
                    break;
                }

                calendar.add(newDate);
                setDuelsCalendar.addAll(newDate.getGames());
            }
        //}//else{
            /*
            Este Caso de Uso es cuando se solicita generar un calendario del tipo series de 3-2 juegos
             */
        //}
        return calendar;
    }

    private void fillFirstDateSlow(Date date, ArrayList<ArrayList<Integer>> duelsCopy){

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
        duelsCopy.removeAll(dateToAdd.getGames());
    }

    private void fillFirstDateFast(Date date, ArrayList<ArrayList<Integer>> duelsCopy, Set<ArrayList<Integer>> setDuelsCalendar){

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
        setDuelsCalendar.addAll(dateToAdd.getGames());

        //duelsCopy.removeAll(dateToAdd.getGames());
    }

    private int posLessDistanceDuel(ArrayList<Date> calendar, Date date, Date newDate, ArrayList<ArrayList<Integer>> avalibleDuels){
        int pos = -1;
        double distance = Double.MAX_VALUE;
        for(ArrayList<Integer> gameNow: avalibleDuels){

            boolean existsNewDate = false;
            int i = 0;
            while (!existsNewDate && i < newDate.getGames().size()){

                if (newDate.getGames().get(i).contains(gameNow.get(0)) || newDate.getGames().get(i).contains(gameNow.get(1))){
                    existsNewDate = true;
                }
                i++;
            }

            boolean existsBeforeDates = false;
            for (int j = 0; j < calendar.size(); j++) {
                if (calendar.get(j).getGames().contains(gameNow)){
                    existsBeforeDates = true;
                }
            }



            if (!existsNewDate && !existsBeforeDates){
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

    private ArrayList<ArrayList<Integer>> lessDistanceDuels(Set<ArrayList<Integer>> setDuelsCalendar, Set<Integer> setTeamsDate, Date date, ArrayList<ArrayList<Integer>> avalibleDuels){

        ArrayList<AuxDuelDistanceAdded> list = new ArrayList<>();
        ArrayList<ArrayList<Integer>> listOrdered = new ArrayList<>();

        for(ArrayList<Integer> gameNow: avalibleDuels){

            boolean existsNewDate = false;

            if (setTeamsDate.contains(gameNow.get(0)) || setTeamsDate.contains(gameNow.get(1))){
                existsNewDate = true;
            }

            boolean existsBeforeDates = false;
            if (setDuelsCalendar.contains(gameNow)){
                existsBeforeDates = true;
            }

            if (!existsNewDate && !existsBeforeDates){
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
                AuxDuelDistanceAdded temp = new AuxDuelDistanceAdded(gameNow, dist);

                list.add(temp);
            }
        }


        if (!list.isEmpty()){
            quickSort(list, 0, list.size()-1);
        }

        for (AuxDuelDistanceAdded temp: list) {
            listOrdered.add(temp.getDuel());
        }

        return listOrdered;
    }

    private void quickSort(ArrayList<AuxDuelDistanceAdded> list, int left, int right){
        double pivot = list.get(left).getDistance();
        int i = left;
        int j = right;
        AuxDuelDistanceAdded aux;

        while (i < j){
            while (list.get(i).getDistance() <= pivot && i < j){
                i++;
            }
            while (list.get(j).getDistance() > pivot){
                j--;
            }
            if(i < j){
                aux = list.get(i);
                list.set(i, list.get(j));
                list.set(j, aux);
            }
        }
        aux = list.get(left);
        list.set(left, list.get(j));
        list.set(j, aux);

        if(left < j - 1){
            quickSort(list, left, j-1);
        }
        if (j+1 < right){
            quickSort(list, j+1, right);
        }
    }
}
