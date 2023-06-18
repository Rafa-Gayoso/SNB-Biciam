package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IChampionGame;
import problem.definition.State;

import java.util.ArrayList;

public class TwoSubseriesDoubleRoundNonSymmetricChampionSolution extends TwoSubseriesDoubleRoundNonSymmetricSolution implements IChampionGame {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        splitSeries(state);
        fixChampionSubchampion(state);

        return state;
    }
}