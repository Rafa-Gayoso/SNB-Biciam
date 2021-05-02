package operators.initialSolution;

import definition.state.statecode.Date;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;

import java.util.ArrayList;

public class DoubleRoundNonSymmetricOddSolution extends InitialSolution {
    @Override
    protected State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {
        return null;
    }

    protected void setSecondRound(State state) {
        State newState = state.clone();
        generateSecondRound(newState);
        state.getCode().addAll(newState.getCode());
    }

    protected void generateSecondRound(State state) {

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
