package operators.interfaces;


import definition.TTPDefinition;
import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface IInauguralGame {

    default void addInauguralGame(State state){
        Date inauguralDate = new Date();
        ArrayList<Integer> pair = new ArrayList<>();
        pair.add(TTPDefinition.getInstance().getFirstPlace());
        pair.add(TTPDefinition.getInstance().getSecondPlace());
        inauguralDate.getGames().add(pair);
        state.getCode().add(0, inauguralDate);
    }
}
