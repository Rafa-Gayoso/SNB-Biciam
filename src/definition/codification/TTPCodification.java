package definition.codification;

import definition.TTPDefinition;
import problem.definition.Codification;
import problem.definition.State;

import java.util.Random;

public class TTPCodification extends Codification {
    @Override
    public boolean validState(State state) {
        int cantVecesLocales = TTPDefinition.getInstance().penalizeLocalGames(state);
        int cantVecesVisitante = TTPDefinition.getInstance().penalizeVisitorGames(state);
        boolean valid =true;
        if(cantVecesLocales > 0 || cantVecesVisitante > 0){
            valid = false;
        }
        return valid;
    }

    @Override
    public Object getVariableAleatoryValue(int i)
    {

        return null;
    }

    @Override
    public int getAleatoryKey() {
        //la posicion de una variable de la solución es una de las fechas
        Random random = new Random();
        return random.nextInt(TTPDefinition.getInstance().getCantFechas());
    }

    @Override
    public int getVariableCount()
    {
        return TTPDefinition.getInstance().getCantFechas();
    }


}
