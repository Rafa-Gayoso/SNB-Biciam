package operators.initialSolution;

import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IChampionGame;
import operators.interfaces.ILongShortSeries;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;

public class LSSDoubleRoundNonSymmetricChampionSolution extends LSSDoubleRoundNonSymmetricSolution implements IChampionGame {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics){
        State state = super.generateCalendar(heuristics);

        splitSeries(state);
        fixChampionSubchampion(state);

        return state;
    }
}