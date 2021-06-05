package execute;

import definition.TTPDefinition;
import definition.codification.TTPCodification;
import definition.objective.function.TTPObjectiveFunction;
import definition.operator.TTPOperator;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneratorType;
import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IChampionGame;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import operators.mutation.MutationOperatorType;
import problem.definition.ObjetiveFunction;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import problem.extension.TypeSolutionMethod;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.AuxStatePlusIterations;
import utils.CalendarConfiguration;
import utils.DataFiles;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executer implements ISecondRound, IInauguralGame, IChampionGame {

    private int ITERATIONS;
    private int EXECUTIONS;
    private Problem problem;
    private ArrayList<MutationOperatorType> mutations;
    private ArrayList<HeuristicOperatorType> heuristics;
    private int selectedMH;// 0 - HillCimbing, 1 - Evolution Strategy  other - RandomSearch
    private ArrayList<AuxStatePlusIterations> saveData;

    private static Executer executerInstance;
    private Map<String, Integer> idMaps;
    private List<State> resultStates;
    private boolean timeToSetDateToStart;

    private Executer(){
        this.resultStates = new ArrayList<>();
        this.mutations = new ArrayList<>();
        this.heuristics = new ArrayList<>();
        this.saveData = new ArrayList<>();
        this.EXECUTIONS = 10;
        this.ITERATIONS = 20000;
        this.selectedMH = 0;
        this.idMaps = new HashMap<>();
        this.timeToSetDateToStart = false;
    }

    public static Executer getInstance(){
        if(executerInstance == null){
            executerInstance = new Executer();
        }
        return executerInstance;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }


    /**
     * Configura el problema estableciendo la funcion objetivo, el operador,
     * solucion inicial, tipo de problema, codificacion.
     */
    public void configureProblem() {

        problem = new Problem();//Instancia del problema a resolver
        TTPObjectiveFunction objectiveFunction = new TTPObjectiveFunction();//Se instancia la funcion obj del problema
        ArrayList<ObjetiveFunction> listObj = new ArrayList<>();
        listObj.add(objectiveFunction);
        TTPCodification codification = new TTPCodification();//se instancia la codificacion a utilizar
        Operator operator = new TTPOperator(mutations, heuristics);//Se instancia el operador
        problem.setCodification(codification);//Se establece la codificacion a emplear
        problem.setFunction(listObj);//Se le pasa al problema la lista de funciones a objetivo a optimizar, en este caso una sola funcion objetivo
        problem.setOperator(operator);//Se establece la instancia de la clase Operator para el problema
        problem.setTypeProblem(Problem.ProblemType.Minimizar);//Se establece el objetivo del problema
        problem.setCountPeriods(1);//Esto es para el caso en que el problema sea dinamico
        problem.setTypeSolutionMethod(TypeSolutionMethod.MonoObjetivo);//Se establece el tipo de problema
    }

    public void executeEC() throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException, IOException {
        configureProblem();

        /*String nameMH = "";
        if (selectedMH == 0){
            nameMH = "EC";
        }else if (selectedMH == 1){
            nameMH = "EE";
        }else{
            nameMH = "RS";
        }

        String rounds = "";
        if (TTPDefinition.getInstance().isSecondRound()){
            rounds = "Doble";
        }else {
            rounds = "Simple";
        }

        int cantEquipos = TTPDefinition.getInstance().getCantEquipos();

        String fileName = nameMH+"_"+rounds+"_"+cantEquipos+"-"+"Teams"+"_"+EXECUTIONS+"-"+"Exec"+"_"+ITERATIONS+"-"+"Ite";

        File file = new File("src/files/"+fileName+".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();

        ArrayList<State> thisLapBests = new ArrayList<>();*/
        for (int i = 0; i < EXECUTIONS; i++) {

            if(timeToSetDateToStart){
                TTPDefinition.getInstance().setDateToStart(TTPDefinition.getInstance().getDateToStartList().get(i));
            }

            Strategy.getStrategy().setStopexecute(new StopExecute());
            Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
            Strategy.getStrategy().setProblem(this.problem);
            Strategy.getStrategy().saveListBestStates = true;
            Strategy.getStrategy().saveListStates = true;
            Strategy.getStrategy().calculateTime = true;
            if(selectedMH == 0){

                Strategy.getStrategy().executeStrategy(ITERATIONS, 1, GeneratorType.HillClimbing);
                System.err.println("HC-------");
            }
            else{
                if(selectedMH == 1){
                    EvolutionStrategies.countRef = 4;
                    EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                    EvolutionStrategies.mutationType = MutationType.GenericMutation;
                    EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                    EvolutionStrategies.PM = 0.8;
                    
                    System.err.println("EE-------RS");
                }
                else
                    EvolutionStrategies.countRef = 0;

                Strategy.getStrategy().executeStrategy(ITERATIONS,1, GeneratorType.RandomSearch);
                System.err.println("RS-------");
            }


            //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
            //thisLapBests.add(Strategy.getStrategy().getBestState());
            CalendarState state = (CalendarState) Strategy.getStrategy().getBestState();

            CalendarConfiguration configuration = state.getConfiguration();

            if(configuration.isSymmetricSecondRound()){
                deleteInauguralGame(state);
                setSecondRound(state);
                if (configuration.isChampionVsSecondPlace()) {
                    if (configuration.isInauguralGame())
                        addInauguralGame(state);
                    else
                        fixChampionSubchampion(state);
                }
            }

            if (!TTPDefinition.getInstance().isOccidentVsOrient()){
                if( Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()) == null){
                    Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(), 1);
                }else{
                    ;
                    Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(),
                            Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId())+1);

                }
                state.getConfiguration().setCalendarId(TTPDefinition.getInstance().getCalendarId() +"."+
                        Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()));

                if( Executer.getInstance().getIdMaps().get(state.getConfiguration().getCalendarId()) == null){
                    Executer.getInstance().getIdMaps().put(state.getConfiguration().getCalendarId(), 1);
                }else{
                    Executer.getInstance().getIdMaps().put(state.getConfiguration().getCalendarId(),
                            Executer.getInstance().getIdMaps().get(state.getConfiguration().getCalendarId())+1);
                }
            }

            resultStates.add(state);
            //resultStates.add(Strategy.getStrategy().getBestState());
            Strategy.destroyExecute();
        }
    }




    public ArrayList<MutationOperatorType> getMutations() {
        return mutations;
    }

    public void setMutations(ArrayList<MutationOperatorType> mutations) {
        this.mutations = mutations;
    }

    public List<State> getResultStates() {
        return resultStates;
    }

    public void setResultStates(List<State> resultStates) {
        this.resultStates = resultStates;
    }

    public ArrayList<HeuristicOperatorType> getHeuristics() {
        return heuristics;
    }

    public void setHeuristics(ArrayList<HeuristicOperatorType> heuristics) {
        this.heuristics = heuristics;
    }

    public int getITERATIONS() {
        return ITERATIONS;
    }

    public void setITERATIONS(int ITERATIONS) {
        this.ITERATIONS = ITERATIONS;
    }

    public int getEXECUTIONS() {
        return EXECUTIONS;
    }

    public void setEXECUTIONS(int EXECUTIONS) {
        this.EXECUTIONS = EXECUTIONS;
    }

    public int getSelectedMH() {
        return selectedMH;
    }

    public void setSelectedMH(int selectedMH) {
        this.selectedMH = selectedMH;
    }

    public Map<String, Integer> getIdMaps() {
        return idMaps;
    }

    public void setIdMaps(Map<String, Integer> idMaps) {
        this.idMaps = idMaps;
    }

    public boolean isTimeToSetDateToStart() {
        return timeToSetDateToStart;
    }

    public void setTimeToSetDateToStart(boolean timeToSetDateToStart) {
        this.timeToSetDateToStart = timeToSetDateToStart;
    }
}
