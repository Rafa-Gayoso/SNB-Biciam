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
    },

    CHANGE_TEAMS_OPERATOR{


        @Override
        public String toString() {

            return ChangeTeamsOperator.class.getName();
        }
    },


    CHANGE_DATE_DUELS_ORDER_OPERATOR{


        @Override
        public String toString() {

            return ChangeDateDuelsOrderOperator.class.getName();
        }
    },

    CHANGE_DATE_SINGLE_DUEL_ORDER_OPERATOR{


        @Override
        public String toString() {

            return ChangeDateSingleDuelOrderOperator.class.getName();
        }

    },
    CHANGE_LOCAL_VISITOR_SINGLE_TEAM_OPERATOR{


        @Override
        public String toString() {

            return ChangeLocalVisitorSingleTeamOperator.class.getName();
        }
    },


}
