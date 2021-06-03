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


}
