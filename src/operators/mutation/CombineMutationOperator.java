package operators.mutation;

import operators.factory.MutationOperatorFactory;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Random;

public class CombineMutationOperator extends MutationOperator {
    protected ArrayList<MutationOperatorType> types;



    @Override
    public State applyMutation(State state) {

        Random random = new Random();
        int randomNumber = random.nextInt(types.size());
        MutationOperator mutation = MutationOperatorFactory.getInstance(types.get(randomNumber));
        State finalState = mutation.applyMutation(state);
        return finalState;
    }

    public ArrayList<MutationOperatorType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<MutationOperatorType> types) {
        this.types = types;
    }
}
