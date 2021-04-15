package operators.mutation;

import operators.interfaces.ICopyState;
import problem.definition.State;

public abstract class MutationOperator implements ICopyState {



    public abstract State applyMutation(State state);



}
