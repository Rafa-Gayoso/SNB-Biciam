package operators.mutation;

public enum MutationOperatorType {

    CHANGE_DATE_POSITION{

        @Override
        public String toString() {

            return ChangeDatePositionOperator.class.getName();
        }
    },

    SWAP_DATES{
        @Override
        public String toString() {

            return SwapDatesOperator.class.getName();
        }
    },

    CHANGE_DATE_ORDER{

        @Override
        public String toString() {
            return ChangeDateOrderOperator.class.getName();
        }
    },

    CHANGE_DUEL{


        @Override
        public String toString() {

            return ChangeDuelOperator.class.getName();
        }
    };

}
