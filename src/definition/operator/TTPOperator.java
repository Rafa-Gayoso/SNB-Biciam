package definition.operator;

import definition.TTPDefinition;
import operators.crossover.CrossoverOperator;
import operators.factory.InitialSolutionFactory;
import operators.heuristics.HeuristicOperatorType;
import operators.initialSolution.InitialSolution;
import operators.interfaces.IChampionGame;
import operators.interfaces.ICreateInitialSolution;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import operators.mutation.CombineMutationOperator;
import operators.mutation.MutationOperatorType;
import problem.definition.Operator;
import problem.definition.State;

import java.util.ArrayList;
import java.util.List;

public class TTPOperator extends Operator implements ICreateInitialSolution, ISecondRound, IInauguralGame, IChampionGame {

    private InitialSolution initialSolution;
    private CombineMutationOperator operatorSelector;
    private CrossoverOperator crossoverOperator;
    private ArrayList<HeuristicOperatorType> heuristics;

    public TTPOperator(ArrayList<MutationOperatorType> mutations, ArrayList<HeuristicOperatorType> heuristics){

        this.operatorSelector = new CombineMutationOperator();
        this.heuristics = heuristics;
        this.crossoverOperator = new CrossoverOperator();
        operatorSelector.setTypes(mutations);
    }

    @Override
    public List<State> generatedNewState(State state, Integer neighborhoodSize) {
        List<State> neighborhood = new ArrayList<>(neighborhoodSize);
        for (int i = 0; i <  neighborhoodSize; i++) {
            State newState = operatorSelector.applyMutation(state);
            if(TTPDefinition.getInstance().isSymmetricSecondRound()){
                setSecondRound(newState);
                if(TTPDefinition.getInstance().isChampionVsSub()){
                    if(TTPDefinition.getInstance().isInauguralGame())
                        addInauguralGame(newState);
                    else
                        fixChampionSubchampion(newState);
                }
            }
            neighborhood.add(newState);
        }
        return neighborhood;
    }

    @Override
    public List<State> generateRandomState(Integer neighborhoodSize) {
        List<State> neighborhood = new ArrayList<>(neighborhoodSize);
        this.initialSolution = InitialSolutionFactory.getInstance(createSolutionType());
        for (int i = 0; i < neighborhoodSize; i++) {
            State state = initialSolution.generateCalendar(heuristics);
            neighborhood.add(state);
        }
        return neighborhood;
    }

    @Override
    public List<State> generateNewStateByCrossover(State state, State state1) {
        List<State> newStates = new ArrayList<>();
        State crossedState =  crossoverOperator.createNewStateByCrossover(state, state1);
        newStates.add(crossedState);
        return newStates;
    }
}
