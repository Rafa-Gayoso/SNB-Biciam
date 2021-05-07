package operators.mutation;

import definition.TTPDefinition;
import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeLocalVisitorSingleTeamOperator extends MutationOperator {
    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();

        int selectedTeam = -1;

        /*if (!configurationsList.isEmpty()) {
            selectedTeam = configurationsList.get(number).get(2);
        }*/

        if (selectedTeam == -1) {
            selectedTeam = ThreadLocalRandom.current().nextInt(0, TTPDefinition.getInstance().getCantEquipos());
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
