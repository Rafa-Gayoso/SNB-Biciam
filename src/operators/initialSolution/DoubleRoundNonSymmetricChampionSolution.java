package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IChampionGame;
import problem.definition.State;

import java.util.ArrayList;

public class DoubleRoundNonSymmetricChampionSolution extends DoubleRoundNonSymmetricSolution implements IChampionGame {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {
        State state = super.generateCalendar(heuristics);
        fixChampionSubchampion(state);

        return state;
    }
}
