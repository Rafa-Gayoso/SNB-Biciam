package operators.mutation;

import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import operators.interfaces.ICopyState;
import problem.definition.State;

import java.util.concurrent.ThreadLocalRandom;

public class SwapDatesOperator extends MutationOperator implements ICopyState {
    @Override
    public State applyMutation(State state) {
        State resultState = new State();
        copyState(resultState,state);
        int firstDate = -1;
        int secondDate = -1;
        int startPosition = 0;

        if(TTPDefinition.getInstance().isInauguralGame()){
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

        if(resultState.getCode().size()<15){
            System.out.println("ELIMINO");
        }
        return resultState;
    }
}
