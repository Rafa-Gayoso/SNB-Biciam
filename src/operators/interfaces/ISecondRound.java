package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface ISecondRound {

    default void setSecondRound(State state) {
        State newState = state.clone();
        generateSecondRound(newState);
        state.getCode().addAll(newState.getCode());
    }

    default void generateSecondRound(State state) {

        for (int i=0; i < state.getCode().size();i++){
            Date date = (Date) state.getCode().get(i);
            for (int j = 0; j < date.getGames().size(); j++) {
                ArrayList<Integer> duel = date.getGames().get(j);
                int local = duel.get(0);
                int visitor = duel.get(1);
                duel.set(0, visitor);
                duel.set(1, local);
            }
        }
    }
}
