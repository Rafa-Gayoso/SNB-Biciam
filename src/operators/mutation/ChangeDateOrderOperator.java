package operators.mutation;

import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDateOrderOperator extends MutationOperator {

    @Override
    public State applyMutation(State state) {
        State resultState = state.clone();
        //copyState(resultState,state);
        int firstDate = -1;
        int lastDate = -1;
        int startPosition = 0;


        if (firstDate == -1) {
            firstDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size() - 1);
        }

        if (lastDate == -1) {
            while (lastDate <= firstDate) {
                lastDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
            }
        }

        Deque<Date> stack = new ArrayDeque<>();

        for (int i = firstDate; i <= lastDate; i++) {
            Date date = (Date)resultState.getCode().get(i);

            stack.push(date);
        }
        for (int i = firstDate; i <= lastDate; i++) {
            Date date = stack.poll();
            resultState.getCode().set(i, date);
        }
        return resultState;
    }
}
