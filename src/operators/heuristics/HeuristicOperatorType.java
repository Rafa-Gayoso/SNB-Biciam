package operators.heuristics;

import operators.mutation.ChangeDateOrderOperator;
import operators.mutation.ChangeDatePositionOperator;
import operators.mutation.ChangeDuelOperator;
import operators.mutation.SwapDatesOperator;

public enum  HeuristicOperatorType {

    DUEL_HEURISTIC{

        @Override
        public String toString() {

            return DuelHeuristicOperator.class.getName();
        }
    },

    DATE_HEURISTIC{
        @Override
        public String toString() {

            return DateHeuristicOperator.class.getName();
        }
    };


}
