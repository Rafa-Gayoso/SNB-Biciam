package operators.factory;


import operators.mutation.MutationOperatorType;
import operators.mutation.MutationOperator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MutationOperatorFactory {

    private MutationOperatorFactory(){

    }

    public static MutationOperator getInstance(MutationOperatorType type){
        MutationOperator operator =null;
        try{

            operator = (MutationOperator) MutationOperator.class.getClassLoader().loadClass(type.toString()).newInstance();
        }catch (IllegalAccessException | ClassNotFoundException | InstantiationException e){
            Logger.getLogger(MutationOperator.class.getName()).log(Level.SEVERE, null, e);
        }

        return operator;
    }
}
