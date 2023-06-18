package operators.interfaces;

import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface ISecondRound {

    default void setSecondRound(State state) {

        //DEBUG
        System.out.println("ISecondRound.setSecondRound()");

        State newState = state.clone();

        //DEBUG
        System.out.println("\tsize of state: "+state.getCode().size());

        generateSecondRound(newState);

        ArrayList<Date> dates = getDates(newState);
        state.getCode().addAll(dates);

        //DEBUG
        System.out.println("\tsize of newState: "+state.getCode().size());
    }

    default void generateSecondRound(State state) {

        for (int i = 0; i < state.getCode().size(); i++) { //Por cada fecha del estado
            Date date = (Date) state.getCode().get(i);
            for (int j = 0; j < date.getGames().size(); j++) { //Por cada duelo de la fecha
                ArrayList<Integer> duel = date.getGames().get(j);
                int local = duel.get(0);
                int visitor = duel.get(1);
                duel.set(0, visitor);
                duel.set(1, local);
            }
        }
    }


    default  ArrayList<Date> getDates(State newState){
        ArrayList<Date> fechas = new ArrayList<>();
        try{
            for(Object object:newState.getCode()){
                Date date = (Date)object;
                fechas.add(date.clone());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return fechas;
    }

    default void deleteSecondRound(State newState){
        CalendarConfiguration configuration = ((CalendarState)newState).getConfiguration();

        int teams = configuration.getTeamsIndexes().size();

        for(int i=newState.getCode().size()-1; i >=teams-1; i--){
            newState.getCode().remove(i);
        }
    }
}
