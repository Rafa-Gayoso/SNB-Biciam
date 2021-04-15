package operators.factory;

import operators.heuristics.HeuristicOperator;
import operators.heuristics.HeuristicOperatorType;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeuristicOperatorFactory {

    private HeuristicOperatorFactory(){

    }

    public static HeuristicOperator getInstance(HeuristicOperatorType type){
        HeuristicOperator operator =null;
        try{
            operator = (HeuristicOperator) HeuristicOperator.class.getClassLoader().loadClass(type.toString()).newInstance();
        }catch (IllegalAccessException | ClassNotFoundException | InstantiationException e){
            Logger.getLogger(HeuristicOperator.class.getName()).log(Level.SEVERE, null, e);
        }

        return operator;
    }
}
