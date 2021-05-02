package operators.initialSolution;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;

import java.util.ArrayList;

public abstract class InitialSolution {

    protected int [][] duelMatrix;
    protected ArrayList<ArrayList<Integer>> duels;


    protected abstract State generateCalendar(ArrayList<HeuristicOperatorType> heuristics);

       
}
