package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

public interface ILongShortSeries {
    default void splitSeries(State state) {
        int size = state.getCode().size();
        for (int i = size; i >= 0; i--){
            Date dateClone = null;
            try {
                dateClone = ((Date) state.getCode().get(i)).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            state.getCode().add(i,dateClone);
        }
    }
}
