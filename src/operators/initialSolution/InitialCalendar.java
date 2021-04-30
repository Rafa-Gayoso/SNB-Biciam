package operators.initialSolution;

import definition.TTPDefinition;
import definition.state.statecode.Date;
import operators.factory.HeuristicOperatorFactory;
import operators.heuristics.HeuristicOperator;
import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.ICopyState;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Random;

public class InitialCalendar implements ICopyState {

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

        State state = new State();
        boolean good = false;
        while (!good){
            state.getCode().addAll(heuristic.generateCalendar(duels));
            if (state.getCode().size() == newMatrix.length-1){
                good = true;
            }
            else {
                state = new State();
            }
        }

        //ArrayList<Object> codes = new ArrayList<>();
        /*int[][] newMatrix = new int[duelMatrix.length][duelMatrix.length];

        for (int i = 0; i < duelMatrix.length; i++) {
            for (int j = 0; j < duelMatrix.length; j++) {
                newMatrix[i][j] = duelMatrix[i][j];
            }
        }

        ArrayList<Integer> teamsIndexes = TTPDefinition.getInstance().getTeamsIndexes();

        int dates = teamsIndexes.size()-1;//30;
        for (int f = 0; f < dates; f++) {
            int j = 0;
            int lastLocal = 0;

            Date date = new Date(teamsIndexes.size() / 2);


            for (int i = 0; i < newMatrix.length; i++) {
                for (; j < newMatrix[i].length; j++) {
                    if (i < j) {
                        if (newMatrix[i][j] != 0) {
                            boolean isIn = isInDate(teamsIndexes.get(i), teamsIndexes.get(j), date);
                            if (!isIn) {
                                ArrayList<Integer> pair = new ArrayList<>(2);
                                if (newMatrix[i][j] == 1) {
                                    pair.add(teamsIndexes.get(j));
                                    pair.add(teamsIndexes.get(i));
                                } else {
                                    pair.add(teamsIndexes.get(i));
                                    pair.add(teamsIndexes.get(j));
                                }
                                date.getGames().add(pair);
                                lastLocal = newMatrix[i][j];
                                newMatrix[i][j] = 0;
                            }
                        }
                    }
                }
                if (i == newMatrix.length - 1) {
                    if (date.getGames().size() != (teamsIndexes.size() / 2)) {

                        int local = teamsIndexes.indexOf(date.getGames().get(date.getGames().size() - 1).get(0));
                        int visitor = teamsIndexes.indexOf(date.getGames().get(date.getGames().size() - 1).get(1));

                        if (local < visitor) {
                            i = local;
                            j = visitor;
                        } else {
                            i = visitor;
                            j = local;
                        }

                        newMatrix[i][j] = lastLocal;
                        date.getGames().remove(date.getGames().size() - 1);

                        i--;
                        j++;
                    }
                } else {
                    j = 0;
                }
            }
            codes.add(date);
        }*/

        //state.setCode(codes);

        if(TTPDefinition.getInstance().isDobleVuelta()){
            setSecondRound(state);
        }

        return state;
    }

    private boolean isInDate(int row, int col, Date date) {
        boolean isIn = false;
        int i = 0;
        while (i < date.getGames().size() && !isIn) {
            int j = 0;
            while (j < date.getGames().get(i).size() && !isIn) {
                if (date.getGames().get(i).get(j) == row || date.getGames().get(i).get(j) == col)
                    isIn = true;
                j++;
            }
            i++;
        }
        return isIn;
    }

    private int[][] symmetricCalendar(int[][] matrix) {

        ArrayList<ArrayList<Integer>> cantLocalsAndVisitorsPerRow = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++){
            ArrayList<Integer> row = new ArrayList<>();
            row.add(0);
            row.add(0);
            cantLocalsAndVisitorsPerRow.add(row);
        }


        int cantMaxLocalOrVisitor = matrix.length / 2;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i != j) {
                    if (matrix[i][j] == 0) {
                        if (cantLocalsAndVisitorsPerRow.get(i).get(0) < cantMaxLocalOrVisitor) {
                            matrix[i][j] = 1;
                            matrix[j][i] = 2;
                            cantLocalsAndVisitorsPerRow.get(i).set(0, cantLocalsAndVisitorsPerRow.get(i).get(0)+1);
                            cantLocalsAndVisitorsPerRow.get(j).set(1, cantLocalsAndVisitorsPerRow.get(j).get(1)+1);
                        }
                        else {
                            if(cantLocalsAndVisitorsPerRow.get(j).get(0) < cantMaxLocalOrVisitor){
                                matrix[i][j] = 2;
                                matrix[j][i] = 1;
                                cantLocalsAndVisitorsPerRow.get(i).set(1, cantLocalsAndVisitorsPerRow.get(i).get(1)+1);
                                cantLocalsAndVisitorsPerRow.get(j).set(0, cantLocalsAndVisitorsPerRow.get(j).get(0)+1);
                            }
                            else{
                                boolean goBackPossibility = false;
                                int lastRowModified = -1;
                                while (!goBackPossibility && i >= 0){
                                    while (!goBackPossibility & j > 0){
                                        j--;
                                        if (matrix[i][j] == 1){
                                            if (cantLocalsAndVisitorsPerRow.get(i).get(1) < cantMaxLocalOrVisitor && cantLocalsAndVisitorsPerRow.get(j).get(0) < cantMaxLocalOrVisitor){
                                                matrix[i][j] = 2;
                                                matrix[j][i] = 1;

                                                cantLocalsAndVisitorsPerRow.get(i).set(1, cantLocalsAndVisitorsPerRow.get(i).get(1)+1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(0, cantLocalsAndVisitorsPerRow.get(j).get(0)+1);

                                                cantLocalsAndVisitorsPerRow.get(i).set(0, cantLocalsAndVisitorsPerRow.get(i).get(0)-1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(1, cantLocalsAndVisitorsPerRow.get(j).get(1)-1);

                                                goBackPossibility = true;
                                                i = lastRowModified;
                                                j = -1;
                                            }
                                        }
                                        else if(matrix[i][j] == 2){
                                            if (cantLocalsAndVisitorsPerRow.get(i).get(0) < cantMaxLocalOrVisitor && cantLocalsAndVisitorsPerRow.get(j).get(1) < cantMaxLocalOrVisitor){
                                                matrix[i][j] = 1;
                                                matrix[j][i] = 2;

                                                cantLocalsAndVisitorsPerRow.get(i).set(0, cantLocalsAndVisitorsPerRow.get(i).get(0)+1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(1, cantLocalsAndVisitorsPerRow.get(j).get(1)+1);

                                                cantLocalsAndVisitorsPerRow.get(i).set(1, cantLocalsAndVisitorsPerRow.get(i).get(1)-1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(0, cantLocalsAndVisitorsPerRow.get(j).get(0)-1);

                                                goBackPossibility = true;
                                                i = lastRowModified;
                                                j = -1;
                                            }
                                        }
                                        if(!goBackPossibility && matrix[i][j] != 0){
                                            if (matrix[i][j] == 1){
                                                cantLocalsAndVisitorsPerRow.get(i).set(0, cantLocalsAndVisitorsPerRow.get(i).get(0)-1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(1, cantLocalsAndVisitorsPerRow.get(j).get(1)-1);
                                            }
                                            else {
                                                cantLocalsAndVisitorsPerRow.get(i).set(1, cantLocalsAndVisitorsPerRow.get(i).get(1)-1);
                                                cantLocalsAndVisitorsPerRow.get(j).set(0, cantLocalsAndVisitorsPerRow.get(j).get(0)-1);
                                            }
                                            matrix[i][j] = 0;
                                            matrix[j][i] = 0;
                                            if (i < j){
                                                if(i < lastRowModified){
                                                    lastRowModified = i;
                                                }
                                            }
                                            else{
                                                if(j < lastRowModified){
                                                    lastRowModified = j;
                                                }
                                            }
                                        }
                                    }
                                    if (!goBackPossibility){
                                        i--;
                                        j = matrix.length;
                                    }
                                }
                                i = 0;
                            }
                        }
                    }
                }
            }
        }
        return matrix;
    }

    private int[][] generateMatrix() {

        int[][] matrix = symmetricCalendar(duelMatrix);
        return matrix;
    }

    private void setSecondRound(State state) {
        State newState = new State();
        copyState(newState, state);
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
