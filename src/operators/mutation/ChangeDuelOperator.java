package operators.mutation;

import com.sun.prism.shader.Solid_Color_AlphaTest_Loader;
import definition.TTPDefinition;
import definition.state.statecode.Date;
import operators.interfaces.*;
import problem.definition.State;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDuelOperator extends MutationOperator implements ISwapTeams {
    @Override
    public State applyMutation(State state) {
        State resultSate = state.clone();
        int posFirstDate = -1;
        int posLastDate = -1;
        int posFirstDuel = -1;
        int startPosition = 0;

        if(TTPDefinition.getInstance().isInauguralGame()){
            startPosition = 1;
        }
        /*if (!mutationsConfigurationsList.isEmpty()) {
            posFirstDate = mutationsConfigurationsList.get(number).get(0);
            posLastDate = mutationsConfigurationsList.get(number).get(1);
            posFirstDuel = mutationsConfigurationsList.get(number).get(2);

        }*/

        if (posFirstDate == -1) {
            do {
                posFirstDate = ThreadLocalRandom.current().nextInt(startPosition, resultSate.getCode().size());
            }
            while (posLastDate == posFirstDate);
        }

        if (posLastDate == -1) {
            do {
                posLastDate = ThreadLocalRandom.current().nextInt(startPosition, resultSate.getCode().size());
            }
            while (posLastDate == posFirstDate);
        }

        Date firstDate = (Date)resultSate.getCode().get(posFirstDate);
        Date secondDate = (Date)resultSate.getCode().get(posLastDate);

        if (posFirstDuel == -1) {
            do{
                posFirstDuel = ThreadLocalRandom.current().nextInt(0, firstDate.getGames().size());
            }
            while (secondDate.getGames().contains(firstDate.getGames().get(posFirstDuel)));
        }


        swapTeams(posFirstDuel, false, firstDate, secondDate);

        return resultSate ;
    }


}
