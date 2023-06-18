package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.concurrent.ThreadLocalRandom;

public class ChangeTeamsOperator extends MutationOperator{
    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int firstTeam = -1;
        int secondTeam = -1;;



        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            firstTeam = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(4);
            secondTeam = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(5);
        }

        if(firstTeam == -1){
            firstTeam = configuration.getTeamsIndexes().get(ThreadLocalRandom.current().nextInt(0, configuration.getTeamsIndexes().size()));
            secondTeam = firstTeam;

            while (firstTeam == secondTeam) {
                secondTeam = configuration.getTeamsIndexes().get(ThreadLocalRandom.current().nextInt(0, configuration.getTeamsIndexes().size()));
            }
        }





        for (int i = 0; i < resultState.getCode().size(); i++) {
            Date date =  (Date)resultState.getCode().get(i);
            for (int j = 0; j < date.getGames().size(); j++) {

                int local = date.getGames().get(j).get(0);
                int visitor = date.getGames().get(j).get(1);

                if (local == firstTeam) {
                    date.getGames().get(j).set(0, secondTeam);
                    if (visitor == secondTeam) {
                        date.getGames().get(j).set(1, firstTeam);
                    }
                } else if (local == secondTeam) {
                    date.getGames().get(j).set(0, firstTeam);
                    if (visitor == firstTeam) {
                        date.getGames().get(j).set(1, secondTeam);
                    }
                } else if (visitor == firstTeam) {
                    date.getGames().get(j).set(1, secondTeam);
                } else if (visitor == secondTeam) {
                    date.getGames().get(j).set(1, firstTeam);
                }
            }
        }

        return resultState ;

    }
}
