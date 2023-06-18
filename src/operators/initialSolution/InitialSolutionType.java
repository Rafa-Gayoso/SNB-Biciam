package operators.initialSolution;

public enum InitialSolutionType {

    SIMPLE_ROUND_SOLUTION{
        @Override
        public String toString() {

            return SimpleRoundSolution.class.getName();
        }
    },

    OCCIDENT_ORIENT_SOLUTION{
        @Override
        public String toString() {

            return OccidentOrientSolution.class.getName();
        }
    },

    SIMPLE_ROUND_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return SimpleRoundInauguralSolution.class.getName();
        }
    },

    SIMPLE_ROUND_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return SimpleRoundChampionSolution.class.getName();
        }
    },

    DOUBLE_ROUND_NON_SYMMETRIC_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricSolution.class.getName();
        }
    },



    DOUBLE_ROUND_NON_SYMMETRIC_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricInauguralSolution.class.getName();
        }
    },



    DOUBLE_ROUND_NON_SYMMETRIC_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricChampionSolution.class.getName();
        }
    },

    //Initial solutions for 2-3 game series calendarç
    //LSS: Long Short Series. Series of 3 games, and series of 2 games

    LSS_DOUBLE_ROUND_NON_SYMMETRIC_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesDoubleRoundNonSymmetricSolution.class.getName();
        }
    },

    LSS_SIMPLE_ROUND_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesSimpleRoundInauguralSolution.class.getName();
        }
    },
    LSS_SIMPLE_ROUND_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesSimpleRoundChampionSolution.class.getName();
        }
    },
    LSS_DOUBLE_ROUND_NON_SYMMETRIC_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesDoubleRoundNonSymmetricInauguralSolution.class.getName();
        }
    },
    LSS_DOUBLE_ROUND_NON_SYMMETRIC_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesDoubleRoundNonSymmetricChampionSolution.class.getName();
        }
    },
    LSS_SIMPLE_ROUND_SOLUTION{
        @Override
        public String toString() {

            return TwoSubseriesSimpleRoundSolution.class.getName();
        }
    }

}
