package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;

public class DoubleRoundNonSymmetricSolution extends SimpleRoundSolution implements ISecondRound {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {
        State state = super.generateCalendar(heuristics);
        setSecondRound(state);
        return state;
    }
}
