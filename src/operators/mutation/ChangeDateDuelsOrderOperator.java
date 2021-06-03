package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDateDuelsOrderOperator extends MutationOperator{
    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int startPosition = 0;

        if(configuration.isInauguralGame()){
            startPosition = 1;
        }

        int selectedDate =  -1;

        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            selectedDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(0);

        }

        if(selectedDate == -1){
            do {
                selectedDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size() - 1);
            }
            while (selectedDate == -1);
        }



        Date date = (Date) resultState.getCode().get(selectedDate);

        ArrayList<ArrayList<Integer>> temp = (ArrayList<ArrayList<Integer>>) date.getGames().clone();

        for (int i = 0; i < temp.size(); i++) {
            int local = temp.get(i).get(0);
            temp.get(i).set(0, temp.get(i).get(1));
            temp.get(i).set(1, local);
        }

        date.setGames(temp);
        resultState.getCode().set(selectedDate, date);

        //interchangeLocalVisitor(resultState, selectedDate, date, temp);

        if(configuration.isSecondRoundCalendar() && !configuration.isSymmetricSecondRound()){

            for (ArrayList<Integer> duel: date.getGames()) {
                for(int i=0; i < resultState.getCode().size(); i++){
                    if(i != selectedDate){
                        Date pivotDate = (Date) resultState.getCode().get(i);

                        for (ArrayList<Integer> pivotDuel: pivotDate.getGames()) {
                            if(duel.get(0) == pivotDuel.get(0) && duel.get(1) == pivotDuel.get(1)){
                                int local = pivotDuel.get(0);
                                pivotDuel.set(0, pivotDuel.get(1));
                                pivotDuel.set(1, local);
                            }
                        }
                    }
                }
            }

        }
        return resultState;
    }



}
