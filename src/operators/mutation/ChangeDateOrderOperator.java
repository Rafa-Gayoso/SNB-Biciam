package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDateOrderOperator extends MutationOperator {

    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int firstDate = -1;
        int lastDate = -1;
        int startPosition = 0;

        if(configuration.isInauguralGame()){
            startPosition = 1;
        }

        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            firstDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(0);
            lastDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(1);

            if (firstDate > lastDate) {
                int temp = lastDate;
                lastDate = firstDate;
                firstDate = temp;
            }

        }
        if (firstDate == -1) {
            firstDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size() - 1);
        }

        if (lastDate == -1) {
            while (lastDate <= firstDate) {
                lastDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
            }
        }

        Deque<Date> stack = new ArrayDeque<>();

        //NEW for LSS calendars 16-03-2022
        //this is for only take dates of the same parity
        int increment = TTPDefinition.getInstance().isLss() ? 2 : 1;

        for (int i = firstDate; i <= lastDate; i+=increment) {
            Date date = (Date)resultState.getCode().get(i);

            stack.push(date);
        }
        for (int i = firstDate; i <= lastDate; i+=increment) {
            Date date = stack.poll();
            resultState.getCode().set(i, date);
        }

        return resultState;
    }
}
