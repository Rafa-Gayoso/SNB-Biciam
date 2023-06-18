package operators.interfaces;


import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface IInauguralGame {

    default void addInauguralGame(State state){
        Date date = (Date) state.getCode().get(0);
        if(date.getGames().size()>1) {
            Date inauguralDate = new Date();
            ArrayList<Integer> pair = new ArrayList<>();
            pair.add(TTPDefinition.getInstance().getFirstPlace());
            pair.add(TTPDefinition.getInstance().getSecondPlace());
            inauguralDate.getGames().add(pair);
            state.getCode().add(0, inauguralDate);
        }
    }


    default void deleteInauguralGame(State state){

        if(((CalendarState)state).getConfiguration().isInauguralGame())
            state.getCode().remove(0);
    }
}
