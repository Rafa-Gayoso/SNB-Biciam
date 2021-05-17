package operators.mutation;

import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.concurrent.ThreadLocalRandom;

public class SwapDatesOperator extends MutationOperator {
    @Override
    public State applyMutation(State state) {
        State resultState =state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int firstDate = -1;
        int secondDate = -1;
        int startPosition = 0;

        if(configuration.isInauguralGame()){
            startPosition = 1;
        }
        /*if (!mutationsConfigurationsList.isEmpty()) {
            firstDate = mutationsConfigurationsList.get(number).get(0);
            secondDate = mutationsConfigurationsList.get(number).get(1);
        }*/

        if (firstDate == -1) {
            do {
                firstDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
            } while (firstDate == secondDate);
        }

        if (secondDate == -1) {
            do {
                secondDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
            } while (firstDate == secondDate);
        }



        Date auxFirstDate = (Date)resultState.getCode().get(firstDate);
        Date auxSecondDate = (Date)resultState.getCode().get(secondDate);

        resultState.getCode().set(firstDate, auxSecondDate);
        resultState.getCode().set(secondDate, auxFirstDate);


        return resultState;
    }
}
