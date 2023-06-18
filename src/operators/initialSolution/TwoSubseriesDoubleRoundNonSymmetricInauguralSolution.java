package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IInauguralGame;
import problem.definition.State;

import java.util.ArrayList;

public class TwoSubseriesDoubleRoundNonSymmetricInauguralSolution extends TwoSubseriesDoubleRoundNonSymmetricSolution implements IInauguralGame {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        addInauguralGame(state);

        return state;
    }
}