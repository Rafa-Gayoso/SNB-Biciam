package operators.initialSolution;

import definition.TTPDefinition;
import definition.state.CalendarState;
import operators.factory.HeuristicOperatorFactory;
import operators.heuristics.HeuristicOperator;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.Random;

public class SimpleRoundSolution extends InitialSolution {


    
    @Override
    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {

        duels = new ArrayList<>();

        duelMatrix = TTPDefinition.getInstance().getDuelMatrix();

        int[][] newMatrix = new int[duelMatrix.length][duelMatrix.length];

        for (int i = 0; i < duelMatrix.length; i++) {
            for (int j = 0; j < duelMatrix.length; j++) {
                newMatrix[i][j] = duelMatrix[i][j];
            }
        }



        ArrayList<Integer> teamsIndexes = TTPDefinition.getInstance().getTeamsIndexes();

        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                if (i < j) {

                    if(newMatrix[i][j] !=0){
                        ArrayList<Integer> pair = new ArrayList<>(2);
                        if (newMatrix[i][j] == 1) {
                            pair.add(teamsIndexes.get(j));
                            pair.add(teamsIndexes.get(i));
                        } else {
                            pair.add(teamsIndexes.get(i));
                            pair.add(teamsIndexes.get(j));
                        }
                        duels.add(pair);
                    }

                }
            }
        }

        Random random = new Random();
        int randomNumber = random.nextInt(heuristics.size());
        HeuristicOperator heuristic = HeuristicOperatorFactory.getInstance(heuristics.get(randomNumber));

        CalendarState state = new CalendarState();
        CalendarConfiguration configuration;
        if(TTPDefinition.getInstance().getOccidentOrientConfiguration() !=null){
            configuration = TTPDefinition.getInstance().getOccidentOrientConfiguration();
        }else{
            configuration = new CalendarConfiguration(
                    TTPDefinition.getInstance().getCalendarId(), TTPDefinition.getInstance().getTeamsIndexes(), TTPDefinition.getInstance().isInauguralGame(),
                    TTPDefinition.getInstance().isChampionVsSub(), TTPDefinition.getInstance().getFirstPlace(),
                    TTPDefinition.getInstance().getSecondPlace(),TTPDefinition.getInstance().isSecondRound(), TTPDefinition.getInstance().isSymmetricSecondRound(),
                    TTPDefinition.getInstance().isOccidentVsOrient(), TTPDefinition.getInstance().getCantVecesLocal(),
                    TTPDefinition.getInstance().getCantVecesVisitante(), TTPDefinition.getInstance().getRestIndexes(),TTPDefinition.getInstance().getDuelMatrix());
        }

        boolean good = false;


        while (!good){

            state.setConfiguration(configuration);
            state.getCode().addAll(heuristic.generateCalendar(duels));

            if (state.getCode().size() == newMatrix.length-1){
                    good = true;
            }
            else if(TTPDefinition.getInstance().getNumberOfDates() == state.getCode().size()){
                good = true;
            }
            else {
                state = new CalendarState();
            }
        }

        return state;
    }
}
