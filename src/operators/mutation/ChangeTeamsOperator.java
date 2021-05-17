package operators.mutation;

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
        int firstTeam = ThreadLocalRandom.current().nextInt(0, configuration.getTeamsIndexes().size());
        int secondTeam = firstTeam;

        while (firstTeam == secondTeam) {
            secondTeam = ThreadLocalRandom.current().nextInt(0, configuration.getTeamsIndexes().size());
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
