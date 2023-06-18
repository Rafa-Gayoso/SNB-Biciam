package operators.heuristics;

import problem.definition.State;

import java.util.ArrayList;
import definition.state.statecode.Date;

public abstract class HeuristicOperator {

    public abstract ArrayList<Date> generateCalendar(ArrayList<ArrayList<Integer>> duels);

}
