package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface ITwoSubseries {
    default void splitSeries(State state) {

        //DEBUG
        System.out.println("ILongSgortSeries.splitSeries()");

        int size = state.getCode().size();

        ArrayList<Object> code = state.getCode();
        ArrayList<Object> codeClone = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Date dateClone = null;
            try {
                dateClone = ((Date) code.get(i)).clone();
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
            codeClone.add(dateClone);
        }

        ArrayList<Object> finalCode = new ArrayList<>(size*2);
        for(int i = 0; i < size; i++){
            finalCode.add(code.get(i));
            finalCode.add(codeClone.get(i));
        }
        state.setCode(finalCode);
    }
}
