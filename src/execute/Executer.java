package execute;

import definition.codification.TTPCodification;
import definition.objective.function.TTPObjectiveFunction;
import definition.operator.TTPOperator;
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Executer {

    private int ITERATIONS;
    private int EXECUTIONS;
    private Problem problem;
    private ArrayList<MutationOperatorType> mutations;
    private ArrayList<HeuristicOperatorType> heuristics;
    private int selectedMH;// 0 - HillCimbing, 1 - Evolution Strategy  other - RandomSearch
    private ArrayList<AuxStatePlusIterations> saveData;

    private static Executer executerInstance;



    private List<State> resultStates;

    private Executer(){
        this.resultStates = new ArrayList<>();
        this.mutations = new ArrayList<>();
        this.heuristics = new ArrayList<>();
        this.saveData = new ArrayList<>();
        this.EXECUTIONS = 5;
        this.ITERATIONS = 1000;
        this.selectedMH = 0;
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
            resultStates.add(Strategy.getStrategy().getBestState());
            Strategy.destroyExecute();

        }

       // createBestCalendarSheet(workbook, thisLapBests);


       /* FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file.getAbsolutePath());
            workbook.write(fileOut);
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage();
        }*/
    }

   /* private void createBestCalendarSheet(XSSFWorkbook workbook, ArrayList<State> thisLapBests) {
        Sheet spreadsheet = workbook.createSheet("Mejor Calendario ");

        State state = thisLapBests.get(0);
        double dist = Distance.getInstance().calculateCalendarDistance(state);
        int pos = 0;

        for (int i = 0; i < thisLapBests.size(); i++) {
            double tempDist = Distance.getInstance().calculateCalendarDistance(thisLapBests.get(i));
            if (tempDist < dist){
                state = thisLapBests.get(i);
                dist = tempDist;
                pos = i;
            }
        }

        ArrayList<ArrayList<Integer>> teamDate = TTPDefinition.getInstance().teamsItinerary(state);
        Row row = spreadsheet.createRow(0);
        //Style of the cell
        XSSFFont headerCellFont = workbook.createFont();
        headerCellFont.setBold(true);
        headerCellFont.setColor(IndexedColors.WHITE.getIndex());
        headerCellFont.setFontHeightInPoints((short) 15);
        XSSFCellStyle style = workbook.createCellStyle();

        // Setting Background color
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerCellFont);

        //Header of the itinerary
        for (int j = 0; j < teamDate.get(0).size(); j++) {
            int posTeam = teamDate.get(0).get(j);
            String team = DataFiles.getSingletonDataFiles().getTeams().get(posTeam);
            Cell cell = row.createCell(j);
            cell.setCellStyle(style);
            cell.setCellValue(team);
        }

        //Itinerary
        style = workbook.createCellStyle();
        headerCellFont = workbook.createFont();
        headerCellFont.setBold(false);
        headerCellFont.setFontHeightInPoints((short) 12);

        int j = 1;
        for(; j < teamDate.size()-1;j++ ){
            ArrayList<Integer> date = teamDate.get(j);
            row = spreadsheet.createRow(j);
            for(int k=0; k < date.size();k++){
                int posTeam = teamDate.get(j).get(k);
                String team = DataFiles.getSingletonDataFiles().getAcronyms().get(posTeam);
                Cell cell = row.createCell(k);
                cell.setCellStyle(style);
                cell.setCellValue(team);
            }
        }
        for(int l=0; l < row.getLastCellNum(); l++){
            spreadsheet.autoSizeColumn(l);
        }

        row = spreadsheet.createRow(j);
        Cell cell1 = row.createCell(0);
        cell1.setCellValue("Calendario "+(pos+1)+":" );
        Cell cell2 = row.createCell(1);
        cell2.setCellValue(dist);
    }*/

    private static void showMessage() {
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Guardar Resultados");
        notification.setMessage("No se pudo guardar los resultados porque el archivo está en uso.");
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }

    /*private void createCalendarSheet(XSSFWorkbook workbook, State state, int calendar){
        Sheet spreadsheet = workbook.createSheet("Calendario "+ (calendar+1));

        ArrayList<ArrayList<Integer>> teamDate = TTPDefinition.getInstance().teamsItinerary(state);
        Row row = spreadsheet.createRow(0);
        //Style of the cell
        XSSFFont headerCellFont = workbook.createFont();
        headerCellFont.setBold(true);
        headerCellFont.setColor(IndexedColors.WHITE.getIndex());
        headerCellFont.setFontHeightInPoints((short) 15);
        XSSFCellStyle style = workbook.createCellStyle();

        // Setting Background color
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerCellFont);

        //Header of the itinerary
        for (int j = 0; j < teamDate.get(0).size(); j++) {
            int posTeam = teamDate.get(0).get(j);
            String team = DataFiles.getSingletonDataFiles().getTeams().get(posTeam);
            Cell cell = row.createCell(j);
            cell.setCellStyle(style);
            cell.setCellValue(team);
        }

        //Itinerary
        style = workbook.createCellStyle();
        headerCellFont = workbook.createFont();
        headerCellFont.setBold(false);
        headerCellFont.setFontHeightInPoints((short) 12);

        int j = 1;
        for(; j < teamDate.size()-1;j++ ){
            ArrayList<Integer> date = teamDate.get(j);
            row = spreadsheet.createRow(j);
            for(int k=0; k < date.size();k++){
                int posTeam = teamDate.get(j).get(k);
                String team = DataFiles.getSingletonDataFiles().getAcronyms().get(posTeam);
                Cell cell = row.createCell(k);
                cell.setCellStyle(style);
                cell.setCellValue(team);
            }
        }
        for(int l = 0; l < row.getLastCellNum(); l++){
            spreadsheet.autoSizeColumn(l);
        }

        row = spreadsheet.createRow(j);
        Cell cell1 = row.createCell(0);
        cell1.setCellValue("Mejor Resultado: ");
        Cell cell2 = row.createCell(1);
        cell2.setCellValue(Distance.getInstance().calculateCalendarDistance(Strategy.getStrategy().getBestState()));

        j += 2;

        row = spreadsheet.createRow(j);
        Cell cell3 = row.createCell(0);
        cell3.setCellValue("No. de iteracion: ");
        Cell cell4 = row.createCell(1);
        cell4.setCellValue("Distancia (km)");

        j++;

        for (int k = 0; k < Strategy.getStrategy().listBest.size(); k++) {
            row = spreadsheet.createRow(j);
            Cell cellIte = row.createCell(0);
            cellIte.setCellValue("Iteracion: "+ (k+1));
            Cell cellDist = row.createCell(1);
            cellDist.setCellValue(Distance.getInstance().calculateCalendarDistance(Strategy.getStrategy().listBest.get(k)));
            j++;
        }


    }*/

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
}
