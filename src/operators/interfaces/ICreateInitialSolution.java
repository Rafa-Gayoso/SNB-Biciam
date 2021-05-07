package operators.interfaces;


import definition.TTPDefinition;
import operators.initialSolution.InitialSolutionType;

public interface ICreateInitialSolution {

    default InitialSolutionType createSolutionType() {
        InitialSolutionType type = null;

        if (TTPDefinition.getInstance().isOccidentVsOrient()) {


            type = InitialSolutionType.OCCIDENT_ORIENT_SOLUTION;

        } else if (!TTPDefinition.getInstance().isSecondRound()) {
            if (TTPDefinition.getInstance().isInauguralGame()) {
                if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                    type = InitialSolutionType.SIMPLE_ROUND_EVEN_INAUGURAL_SOLUTION;

            } else if (TTPDefinition.getInstance().isChampionVsSub()) {


                if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                    type = InitialSolutionType.SIMPLE_ROUND_EVEN_CHAMPION_SOLUTION;

            } else {
                if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                    type = InitialSolutionType.SIMPLE_ROUND_EVEN_SOLUTION;

            }
        } else {
            if (TTPDefinition.getInstance().isSymmetricSecondRound()) {
                if (TTPDefinition.getInstance().isInauguralGame()) {

                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                        type = InitialSolutionType.DOUBLE_ROUND_SYMMETRIC_EVEN_INAUGURAL_SOLUTION;

                } else if (TTPDefinition.getInstance().isChampionVsSub()) {


                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                        type = InitialSolutionType.DOUBLE_ROUND_SYMMETRIC_EVEN_CHAMPION_SOLUTION;



                } else {
                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                        type = InitialSolutionType.DOUBLE_ROUND_SYMMETRIC_EVEN_SOLUTION;


                }
            } else {
                if (TTPDefinition.getInstance().isInauguralGame()) {

                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_EVEN_INAUGURAL_SOLUTION;

                } else if (TTPDefinition.getInstance().isChampionVsSub()) {


                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_EVEN_CHAMPION_SOLUTION;



                } else {
                    if (TTPDefinition.getInstance().getTeamsIndexes().size() % 2 == 0)

                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_EVEN_SOLUTION;


                }
            }
        }

        return type;
    }
}

