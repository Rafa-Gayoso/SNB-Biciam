package operators.mutation;

import operators.factory.MutationOperatorFactory;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Random;

public class CombineMutationOperator extends MutationOperator {
    protected ArrayList<MutationOperatorType> types;



    @Override
    public State applyMutation(State state) {

        State finalState = new State();
        finalState.setCode(state.getCode());
        Random random = new Random();
        int randomNumber = random.nextInt(types.size());
        MutationOperator mutation = MutationOperatorFactory.getInstance(types.get(randomNumber));
        finalState = mutation.applyMutation(state);
        return finalState;
    }

    public ArrayList<MutationOperatorType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<MutationOperatorType> types) {
        this.types = types;
    }
}
