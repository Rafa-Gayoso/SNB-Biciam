package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ILongShortSeries;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;

public class LSSDoubleRoundNonSymmetricInauguralSolution extends LSSDoubleRoundNonSymmetricSolution implements IInauguralGame {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        addInauguralGame(state);

        return state;
    }
}