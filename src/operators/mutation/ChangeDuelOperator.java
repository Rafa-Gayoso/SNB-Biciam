package operators.mutation;

import com.sun.prism.shader.Solid_Color_AlphaTest_Loader;
import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import operators.interfaces.*;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDuelOperator extends MutationOperator implements ISwapTeams {
    @Override
    public State applyMutation(State state) {

        //DEBUG
        System.out.println("ChangeDuelOperator.applyMutation()");

        State resultState = state.clone();

        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int posFirstDate = -1;
        int posLastDate = -1;
        int posFirstDuel = -1;
        int startPosition = 0;

        if(configuration.isInauguralGame()){
            startPosition = 1;
        }

        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            posFirstDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(0);
            posLastDate =  TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(1);
            posFirstDuel = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(2);
        }


        if (posFirstDate == -1) {
            do {
                //Escoger el indice de Fecha 1 aleatroiamente
                posFirstDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
            }
            while (posLastDate == posFirstDate);
        }

        //Determinar la pariad de la fecha
        boolean OddDate = posFirstDate % 2 == 0;
        boolean EvenDate = !OddDate;

        //DEBUG
        System.out.println("\t posFirstDate"+posFirstDate);


        //Guardar la Fecha 1
        Date firstDate = (Date)resultState.getCode().get(posFirstDate);
        Date secondDate = null;

        if (posFirstDuel == -1) {
            do{

                if (posLastDate == -1) {
                    do {
                        if(!TTPDefinition.getInstance().isLss()){

                            //Escoger Fecha 2 aleatoriamente (Calendario C1)
                            posLastDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());

                            //NEW 14-03-2022 Adding LSSCalendarState Management
                        }else{
                            //Escoger el indice de Fecha 2 de la misma paridad que el indice de Fecha 1
                            posLastDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size()/2);
                            int evenFactor = EvenDate? 1: 0;
                            posLastDate = posLastDate * 2 + evenFactor;
                        }
                    }
                    while (posLastDate == posFirstDate);
                }

                //DEBUG
                System.out.println("\t posLastDate"+posLastDate);

                //Guardar la Fecha 2
                secondDate = (Date)resultState.getCode().get(posLastDate);

                //Escoger un duelo al azar de la Fecha 1
                posFirstDuel = ThreadLocalRandom.current().nextInt(0, firstDate.getGames().size());

                //DEBUG
                System.out.println("\t posFirstDuel "+posFirstDuel);
                System.out.println("\t FirstDuel "+firstDate.getGames().get(posFirstDuel));
                System.out.println("\t secondDate "+secondDate.getGames());
            }
            //Si Fecha 2 Contiene ese juego
            while (secondDate.getGames().contains(firstDate.getGames().get(posFirstDuel)));
        }


        swapTeams(posFirstDuel, false, firstDate, secondDate);

        return resultState ;
    }


}
