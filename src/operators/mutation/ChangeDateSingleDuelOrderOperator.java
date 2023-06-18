package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDateSingleDuelOrderOperator extends MutationOperator {
    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int startPosition = 0;

        if(configuration.isInauguralGame()){
            startPosition = 1;
        }


        int selectedDate = -1;
        int selectedDuel = -1;

        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            selectedDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(0);
            selectedDuel = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(2);

        }


        if (selectedDate == -1) {
            selectedDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size() - 1);
        }
        Date date = (Date) resultState.getCode().get(selectedDate);

        if (selectedDuel == -1) {
            selectedDuel = ThreadLocalRandom.current().nextInt(0, date.getGames().size());
        }

        int temp = date.getGames().get(selectedDuel).get(0);
        date.getGames().get(selectedDuel).set(0, date.getGames().get(selectedDuel).get(1));
        date.getGames().get(selectedDuel).set(1, temp);

        if(configuration.isSecondRoundCalendar() && !configuration.isSymmetricSecondRound()){
            ArrayList<Integer> duel = date.getGames().get(selectedDuel);

            int i=0;
            boolean fixedDuel = false;
            while(i < resultState.getCode().size() && !fixedDuel){
                if(i != selectedDate){
                    Date pivotDate = (Date) resultState.getCode().get(i);

                    for (ArrayList<Integer> pivotDuel: pivotDate.getGames()) {
                        if(duel.get(0) == pivotDuel.get(0) && duel.get(1) == pivotDuel.get(1)){
                            int local = pivotDuel.get(0);
                            pivotDuel.set(0, pivotDuel.get(1));
                            pivotDuel.set(1, local);
                            fixedDuel = true;
                        }
                    }
                }
                i++;
            }

        }
        return resultState;
    }
}
