package operators.interfaces;


import definition.TTPDefinition;
import operators.initialSolution.InitialSolutionType;

public interface ICreateInitialSolution {

    default InitialSolutionType createSolutionType() {
        InitialSolutionType type = null;

        if (!TTPDefinition.getInstance().isOccidentVsOrient()){
            if (!TTPDefinition.getInstance().isSecondRound()) {
                if (TTPDefinition.getInstance().isInauguralGame()) {
                    type = InitialSolutionType.SIMPLE_ROUND_INAUGURAL_SOLUTION;

                } else if (TTPDefinition.getInstance().isChampionVsSub()) {
                    type = InitialSolutionType.SIMPLE_ROUND_CHAMPION_SOLUTION;

                } else {
                    type = InitialSolutionType.SIMPLE_ROUND_SOLUTION;
                }
            } else {
                if (!TTPDefinition.getInstance().isSymmetricSecondRound()) {
                    if (TTPDefinition.getInstance().isInauguralGame()) {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_INAUGURAL_SOLUTION;

                    } else if (TTPDefinition.getInstance().isChampionVsSub()) {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_CHAMPION_SOLUTION;
                    } else {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_SOLUTION;
                    }
                } else {
                    type = InitialSolutionType.SIMPLE_ROUND_SOLUTION;
                }
            }
        }
        else{
            if (!TTPDefinition.getInstance().getOccidentOrientConfiguration().isSecondRoundCalendar()) {
                if (TTPDefinition.getInstance().getOccidentOrientConfiguration().isInauguralGame()) {
                    type = InitialSolutionType.SIMPLE_ROUND_INAUGURAL_SOLUTION;

                } else if (TTPDefinition.getInstance().getOccidentOrientConfiguration().isChampionVsSecondPlace()) {
                    type = InitialSolutionType.SIMPLE_ROUND_CHAMPION_SOLUTION;

                } else {
                    type = InitialSolutionType.SIMPLE_ROUND_SOLUTION;
                }
            } else {
                if (!TTPDefinition.getInstance().getOccidentOrientConfiguration().isSymmetricSecondRound()) {
                    if (TTPDefinition.getInstance().getOccidentOrientConfiguration().isInauguralGame()) {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_INAUGURAL_SOLUTION;

                    } else if (TTPDefinition.getInstance().getOccidentOrientConfiguration().isChampionVsSecondPlace()) {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_CHAMPION_SOLUTION;
                    } else {
                        type = InitialSolutionType.DOUBLE_ROUND_NON_SYMMETRIC_SOLUTION;
                    }
                } else {
                    type = InitialSolutionType.SIMPLE_ROUND_SOLUTION;
                }
            }
        }
        return type;
    }
}

