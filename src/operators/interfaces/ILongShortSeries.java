package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

public interface ILongShortSeries {
    default void splitSeries(State state) throws CloneNotSupportedException {
        int size = state.getCode().size();
        for (int i = size; i >= 0; i--){
            Date dateClone = ((Date) state.getCode().get(i)).clone();
            state.getCode().add(i,dateClone);
        }
    }
}
