package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.ILongShortSeries;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;

public class LSSDoubleRoundNonSymmetricSolution extends DoubleRoundNonSymmetricSolution implements ILongShortSeries {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        splitSeries(state);

        return state;
    }
}