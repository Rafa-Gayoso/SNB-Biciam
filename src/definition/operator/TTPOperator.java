package definition.operator;

import definition.state.CalendarState;
import operators.factory.InitialSolutionFactory;
import operators.heuristics.HeuristicOperatorType;
import operators.initialSolution.InitialSolution;
import operators.initialSolution.InitialSolutionType;
import operators.interfaces.IChampionGame;
import operators.interfaces.ICreateInitialSolution;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import operators.mutation.CombineMutationOperator;
import operators.mutation.MutationOperatorType;
import problem.definition.Operator;
import problem.definition.State;
import utils.CalendarConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TTPOperator extends Operator implements ICreateInitialSolution, ISecondRound, IInauguralGame, IChampionGame {

    private final CombineMutationOperator operatorSelector;

    private final ArrayList<HeuristicOperatorType> heuristics;

    public TTPOperator(ArrayList<MutationOperatorType> mutations, ArrayList<HeuristicOperatorType> heuristics) {

        this.operatorSelector = new CombineMutationOperator();
        this.heuristics = heuristics;
        operatorSelector.setTypes(mutations);
    }

    @Override
    public List<State> generatedNewState(State state, Integer neighborhoodSize) {
        List<State> neighborhood = new ArrayList<>(neighborhoodSize);
        for (int i = 0; i < neighborhoodSize; i++) {
            State newState = operatorSelector.applyMutation(state);
            CalendarConfiguration configuration = ((CalendarState)newState).getConfiguration();

            if (configuration.isChampionVsSecondPlace()) {
                if (configuration.isInauguralGame())
                    addInauguralGame(newState);
                else
                    fixChampionSubchampion(newState);
            }
            neighborhood.add(newState);
        }
        return neighborhood;
    }

    @Override
    public List<State> generateRandomState(Integer neighborhoodSize) {

        //DEBUG
        System.out.println("TTPOperator.generateRandomState()");
        System.out.println("\tneighborhood size: "+neighborhoodSize);

        List<State> neighborhood = new ArrayList<>(neighborhoodSize);
        InitialSolutionType type = createSolutionType();
        InitialSolution initialSolution = InitialSolutionFactory.getInstance(type);
        for (int i = 0; i < neighborhoodSize; i++) {

            //DEBUG
            System.out.println("\tgenerating calendar...");

            State state = initialSolution.generateCalendar(heuristics);

            //DEBUG
                System.out.println("\tcalendar genarated");

            ((CalendarState) state).setCalendarType(type.ordinal());
            CalendarConfiguration configuration = ((CalendarState)state).getConfiguration();
            if (configuration.isChampionVsSecondPlace()) {
                if (configuration.isInauguralGame())
                    addInauguralGame(state);
                else
                    fixChampionSubchampion(state);
            }
            neighborhood.add(state);
        }

        //DEBUG
        System.out.println("TTPOperator.generateRandomState() //finished");

        return neighborhood;
    }

    @Override
    public List<State> generateNewStateByCrossover(State state, State state1) {
        return null;
    }

}
