package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.ITwoSubseries;
import problem.definition.State;

import java.util.ArrayList;

public class TwoSubseriesDoubleRoundNonSymmetricSolution extends DoubleRoundNonSymmetricSolution implements ITwoSubseries {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        splitSeries(state);

        return state;
    }
}