package operators.initialSolution;

import definition.state.CalendarState;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;

import java.util.ArrayList;

public class OccidentOrientSolution extends SimpleRoundSolution {
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {
        State state = super.generateCalendar(heuristics);
        return state;
    }
}
