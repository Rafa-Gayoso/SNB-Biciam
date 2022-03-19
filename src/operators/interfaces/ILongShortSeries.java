package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface ILongShortSeries {
    default void splitSeries(State state) {

        //DEBUG
        System.out.println("ILongSgortSeries.splitSeries()");

        int size = state.getCode().size();
        for (int i = size-1; i >= 0; i--) {
            Date dateClone = null;
            try {
                dateClone = ((Date) state.getCode().get(i)).clone();
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
            state.getCode().add(i, dateClone);
        }
    }
}
