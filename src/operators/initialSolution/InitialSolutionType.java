package operators.initialSolution;

public enum InitialSolutionType {

    SIMPLE_ROUND_EVEN_SOLUTION{
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

    SIMPLE_ROUND_EVEN_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return SimpleRoundInauguralSolution.class.getName();
        }
    },

    SIMPLE_ROUND_EVEN_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return SimpleRoundChampionSolution.class.getName();
        }
    },


    DOUBLE_ROUND_SYMMETRIC_EVEN_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundSymmetricSolution.class.getName();
        }
    },


    DOUBLE_ROUND_SYMMETRIC_EVEN_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundSymmetricInauguralSolution.class.getName();
        }
    },



    DOUBLE_ROUND_SYMMETRIC_EVEN_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundSymmetricChampionSolution.class.getName();
        }
    },



    DOUBLE_ROUND_NON_SYMMETRIC_EVEN_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricSolution.class.getName();
        }
    },



    DOUBLE_ROUND_NON_SYMMETRIC_EVEN_INAUGURAL_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricInauguralSolution.class.getName();
        }
    },



    DOUBLE_ROUND_NON_SYMMETRIC_EVEN_CHAMPION_SOLUTION{
        @Override
        public String toString() {

            return DoubleRoundNonSymmetricChampionSolution.class.getName();
        }
    },


}
