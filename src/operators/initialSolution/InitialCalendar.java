package operators.initialSolution;

import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import operators.factory.HeuristicOperatorFactory;
import operators.heuristics.HeuristicOperator;
import operators.heuristics.HeuristicOperatorType;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Random;

public class InitialCalendar {

    private int [][] duelMatrix;
    private ArrayList<ArrayList<Integer>> duels;
    private static InitialCalendar initialCalendarInstance;

    private InitialCalendar(){

    }

    public static InitialCalendar getInstance(){
        if(initialCalendarInstance == null){
            initialCalendarInstance = new InitialCalendar();
        }
        return initialCalendarInstance;
    }


    public State generateCalendar(ArrayList<HeuristicOperatorType> heuristics) {

        duels = new ArrayList<>();

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


        Random random = new Random();
        int randomNumber = random.nextInt(heuristics.size());
        HeuristicOperator heuristic = HeuristicOperatorFactory.getInstance(heuristics.get(randomNumber));

        CalendarState state = new CalendarState();
        boolean good = false;
        while (!good){
            state.getCode().addAll(heuristic.generateCalendar(duels));
            if (state.getCode().size() == newMatrix.length-1){
                good = true;
            }
            else {
                state = new CalendarState();
            }
        }

        if(TTPDefinition.getInstance().isSecondRound()){
            setSecondRound(state);
        }

        return state;
    }

    private void setSecondRound(State state) {
        State newState = state.clone();
        generateSecondRound(newState);
        state.getCode().addAll(newState.getCode());
    }

    private void generateSecondRound(State state) {

        for (int i=0; i < state.getCode().size();i++){
            Date date = (Date) state.getCode().get(i);
            for (int j = 0; j < date.getGames().size(); j++) {
                ArrayList<Integer> duel = date.getGames().get(j);
                int local = duel.get(0);
                int visitor = duel.get(1);
                duel.set(0, visitor);
                duel.set(1, local);
            }
        }
    }

    public void setDuelMatrix(int [][] duelMatrix){
        this.duelMatrix = duelMatrix;
    }

}
