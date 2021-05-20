package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeLocalVisitorSingleTeamOperator extends MutationOperator {
    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int selectedTeam = -1;



        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            selectedTeam = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(4);
        }

        if (selectedTeam == -1) {
            selectedTeam = ThreadLocalRandom.current().nextInt(0, configuration.getTeamsIndexes().size());
        }


        for (int i = 0; i < state.getCode().size(); i++) {
            Date date = (Date) state.getCode().get(i);
            for (int j = 0; j < date.getGames().size(); j++) {

                int local = date.getGames().get(j).get(0);
                int visitor = date.getGames().get(j).get(1);

                if (local == selectedTeam || visitor == selectedTeam) {

                    date.getGames().get(j).set(0, visitor);
                    date.getGames().get(j).set(1, local);
                }
            }
        }
        return resultState;
    }
}
