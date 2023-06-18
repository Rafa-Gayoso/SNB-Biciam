package operators.initialSolution;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;

import java.util.ArrayList;

public abstract class   InitialSolution {

    protected static int [][] duelMatrix;
    protected ArrayList<ArrayList<Integer>> duels;
    protected State state;



    public abstract State generateCalendar(ArrayList<HeuristicOperatorType> heuristics);

    protected void setDuelMatrix(int[][] duelMatrix){
        this.duelMatrix = duelMatrix;
    }
}
