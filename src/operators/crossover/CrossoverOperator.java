package operators.crossover;

import definition.state.StateWithDistance;
import definition.state.statecode.Date;
import metaheurictics.strategy.Strategy;
import problem.definition.State;

import java.util.ArrayList;
import java.util.Random;

public class CrossoverOperator {

    public State createNewStateByCrossover(State father1, State father2) {
        State crossedState = new State();
        crossFatherStates(father1, father2, crossedState);
        return crossedState;
    }

    private void crossFatherStates(State father1, State father2, State resultState) {

        Random random = new Random();
        int crossoverValue = random.nextInt(Strategy.getStrategy().getProblem().getCodification().getVariableCount());
        ArrayList<Object> firstCode = new ArrayList<>();
        ArrayList<Object> secondCode = new ArrayList<>();
        try {
            for (int i = 0; i < father1.getCode().size(); i++) {
                Date firstFatherDate = (Date) father1.getCode().get(i);
                Date secondFatherDate = (Date) father2.getCode().get(i);

                if (i <= crossoverValue) {
                    firstCode.add(firstFatherDate.clone());
                    secondCode.add(secondFatherDate.clone());
                } else {
                    firstCode.add(secondFatherDate.clone());
                    secondCode.add(firstFatherDate.clone());
                }

            }
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        boolean selectCode = random.nextBoolean();
        resultState.setCode(selectCode ? firstCode : secondCode);


    }
}
