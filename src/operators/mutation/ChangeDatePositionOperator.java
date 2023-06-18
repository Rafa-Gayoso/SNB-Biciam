package operators.mutation;

import controller.MutationsConfigurationController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDatePositionOperator extends MutationOperator {


    @Override
    public State applyMutation(State state) {

        //DEBUG
        System.out.println("ChangeDatePositionOperator.applyMutation()");

        State resultState = state.clone();
        CalendarConfiguration configuration = ((CalendarState)resultState).getConfiguration();
        int selectedDate = -1; //Fecha seleccionada
        int dateToChange = -1; //fecha a cambiar

        int startPosition = 0; //posicion inicial

        if(configuration.isInauguralGame()){ //si es juego inicial, posicion inicial es 1
            startPosition = 1;
        }
        if (!TTPDefinition.getInstance().getMutationsConfigurationsList().isEmpty()) {
            int position = MutationsConfigurationController.currentMutationPostion;
            selectedDate = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(0); //dar un valor a la fecha seleccionada
            dateToChange = TTPDefinition.getInstance().getMutationsConfigurationsList().get(position).get(1); //dar un valor a la fecha a cambiar

        }

        //NEW 14-03-2022
        ArrayList<ArrayList<Object>> code = new ArrayList<ArrayList<Object>>();
        boolean oddDate = false;
        boolean evenDate = false;

        if (selectedDate == -1) { //si fecha seleccionada es
            selectedDate = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());
        }
        boolean OddDate = selectedDate % 2 == 0;
        boolean EvenDate = !OddDate;

        if (dateToChange == -1) {
            do {

                if(!TTPDefinition.getInstance().isLss()){
                    dateToChange = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size());

                //NEW 14-03-2022 Adding LSSCalendarState Management
                }else{
                    dateToChange = ThreadLocalRandom.current().nextInt(startPosition, resultState.getCode().size()/2);
                    int evenFactor = EvenDate? 1: 0;
                    dateToChange = dateToChange * 2 + evenFactor;
                }

            } while ((resultState.getCode().size() > 3) && ((selectedDate - dateToChange) <= 1) && ((selectedDate - dateToChange) >= (-1)));
        }



        Date date = (Date)resultState.getCode().get(selectedDate);

        if (dateToChange < resultState.getCode().size() - 1) {
            if (selectedDate < dateToChange) {
                resultState.getCode().add(dateToChange + 1, date);
            } else {
                resultState.getCode().add(dateToChange, date);
            }
        } else {
            resultState.getCode().add(dateToChange, date);
            int indexAdd = resultState.getCode().size() - 2;
            resultState.getCode().add(indexAdd, resultState.getCode().get(resultState.getCode().size() - 1));
            int indexRemove = resultState.getCode().size() -1;
            resultState.getCode().remove(indexRemove);
        }


        if (dateToChange >= selectedDate) {
            resultState.getCode().remove(selectedDate);
        } else {
            resultState.getCode().remove(selectedDate + 1);
        }


        return resultState;
    }
}
