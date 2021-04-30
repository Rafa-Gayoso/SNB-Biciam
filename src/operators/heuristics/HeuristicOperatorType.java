package operators.heuristics;

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
