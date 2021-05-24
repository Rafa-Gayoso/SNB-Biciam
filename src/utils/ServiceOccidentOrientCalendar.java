package utils;

import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import execute.Executer;
import javafx.concurrent.Task;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneratorType;
import operators.interfaces.IChampionGame;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ServiceOccidentOrientCalendar extends javafx.concurrent.Service<String> implements ISecondRound, IInauguralGame, IChampionGame {
    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                Executer.getInstance().configureProblem();

                int i = 0;
                updateProgress(i, Executer.getInstance().getEXECUTIONS());
                int percent = percent(i);
                updateMessage(percent + " %");
                for (; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    ArrayList<Integer> teamsOnlyOccident = new ArrayList<>();
                    ArrayList<Integer> teamsOnlyOrient = new ArrayList<>();
                    ArrayList<Integer> allTeams = (ArrayList<Integer>) TTPDefinition.getInstance().getTeamsIndexes().clone();

                    int newMatrix[][] = TTPDefinition.getInstance().getDuelMatrix().clone();


                    for (Integer index : TTPDefinition.getInstance().getTeamsIndexes()) {
                        if (DataFiles.getSingletonDataFiles().getLocations().get(index).equalsIgnoreCase("Occidental")) {
                            teamsOnlyOccident.add(index);
                        } else {
                            teamsOnlyOrient.add(index);
                        }
                    }

                    int[][] matrixOnlyOccident = new int[teamsOnlyOccident.size()][teamsOnlyOccident.size()];
                    int[][] matrixOnlyOrient = new int[teamsOnlyOrient.size()][teamsOnlyOrient.size()];

                    for (int k = 0; k < newMatrix.length; k++) {
                        int posIOccident = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(k));
                        int posIOrient = teamsOnlyOrient.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(k));

                        for (int j = 0; j < newMatrix[k].length; j++) {
                            if (k < j) {
                                int posJOccident = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(j));
                                int posJOrient = teamsOnlyOrient.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(j));
                                if (posIOccident != -1 && posJOccident != -1) {
                                    matrixOnlyOccident[posIOccident][posJOccident] = newMatrix[k][j];
                                    matrixOnlyOccident[posJOccident][posIOccident] = newMatrix[j][k];
                                    newMatrix[k][j] = 0;
                                    newMatrix[j][k] = 0;
                                } else if (posIOrient != -1 && posJOrient != -1) {
                                    matrixOnlyOrient[posIOrient][posJOrient] = newMatrix[k][j];
                                    matrixOnlyOrient[posJOrient][posIOrient] = newMatrix[j][k];
                                    newMatrix[k][j] = 0;
                                    newMatrix[j][k] = 0;
                                }
                            }
                        }
                    }

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOccident);
                    TTPDefinition.getInstance().setOccidentOrientCOnConfiguration(new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOccident, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante()));
                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOccident);


                    Strategy.getStrategy().setStopexecute(new StopExecute());
                    Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                    Strategy.getStrategy().setProblem(Executer.getInstance().getProblem());
                    Strategy.getStrategy().saveListBestStates = true;
                    Strategy.getStrategy().saveListStates = true;
                    Strategy.getStrategy().calculateTime = true;
                    if (Executer.getInstance().getSelectedMH() == 0) {

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.HillClimbing);
                        System.err.println("HC-------");
                    } else {
                        if (Executer.getInstance().getSelectedMH() == 1) {
                            EvolutionStrategies.countRef = 4;
                            EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                            EvolutionStrategies.mutationType = MutationType.GenericMutation;
                            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                            EvolutionStrategies.PM = 0.8;

                            System.err.println("EE-------RS");
                        } else
                            EvolutionStrategies.countRef = 0;

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.RandomSearch);
                        System.err.println("RS-------");
                    }


                    //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
                    //thisLapBests.add(Strategy.getStrategy().getBestState());
                    CalendarState stateOccident = (CalendarState) Strategy.getStrategy().getBestState();

                    CalendarConfiguration configuration = stateOccident.getConfiguration();

                    if (configuration.isSymmetricSecondRound()) {
                        deleteInauguralGame(stateOccident);
                        deleteSecondRound(stateOccident);
                        setSecondRound(stateOccident);
                        if (configuration.isChampionVsSecondPlace()) {
                            if (configuration.isInauguralGame())
                                addInauguralGame(stateOccident);
                            else
                                fixChampionSubchampion(stateOccident);
                        }
                    }

                    /*Executer.getInstance().getResultStates().add(Strategy.getStrategy().getBestState());*/
                    Strategy.destroyExecute();

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOrient);
                    TTPDefinition.getInstance().setOccidentOrientCOnConfiguration(new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOrient, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante()));


                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOrient);
                    Strategy.getStrategy().setStopexecute(new StopExecute());
                    Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                    Strategy.getStrategy().setProblem(Executer.getInstance().getProblem());
                    Strategy.getStrategy().saveListBestStates = true;
                    Strategy.getStrategy().saveListStates = true;
                    Strategy.getStrategy().calculateTime = true;
                    if (Executer.getInstance().getSelectedMH() == 0) {

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.HillClimbing);
                        System.err.println("HC-------");
                    } else {
                        if (Executer.getInstance().getSelectedMH() == 1) {
                            EvolutionStrategies.countRef = 4;
                            EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                            EvolutionStrategies.mutationType = MutationType.GenericMutation;
                            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                            EvolutionStrategies.PM = 0.8;

                            System.err.println("EE-------RS");
                        } else
                            EvolutionStrategies.countRef = 0;

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.RandomSearch);
                        System.err.println("RS-------");
                    }


                    //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
                    //thisLapBests.add(Strategy.getStrategy().getBestState());
                    CalendarState stateOrient = (CalendarState) Strategy.getStrategy().getBestState();

                    CalendarConfiguration configurationOrient = stateOrient.getConfiguration();

                    if (configurationOrient.isSymmetricSecondRound()) {
                        deleteInauguralGame(stateOrient);
                        deleteSecondRound(stateOrient);
                        setSecondRound(stateOccident);
                        if (configurationOrient.isChampionVsSecondPlace()) {
                            if (configurationOrient.isInauguralGame())
                                addInauguralGame(stateOrient);
                            else
                                fixChampionSubchampion(stateOrient);
                        }
                    }

                    /*Executer.getInstance().getResultStates().add(Strategy.getStrategy().getBestState());*/
                    Strategy.destroyExecute();

                    TTPDefinition.getInstance().setTeamIndexes(allTeams);
                    TTPDefinition.getInstance().setOccidentOrientCOnConfiguration(new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante()));

                    TTPDefinition.getInstance().setDuelMatrix(newMatrix);

                    int numberOfDate = 0;
                    if (!TTPDefinition.getInstance().isSecondRound()){
                        numberOfDate = (allTeams.size()-1) - stateOccident.getCode().size();
                    }
                    else{
                        numberOfDate = (allTeams.size()-1-1) - (stateOccident.getCode().size()/2);
                    }

                    TTPDefinition.getInstance().setCantFechas(numberOfDate);

                    Strategy.getStrategy().setStopexecute(new StopExecute());
                    Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                    Strategy.getStrategy().setProblem(Executer.getInstance().getProblem());
                    Strategy.getStrategy().saveListBestStates = true;
                    Strategy.getStrategy().saveListStates = true;
                    Strategy.getStrategy().calculateTime = true;
                    if (Executer.getInstance().getSelectedMH() == 0) {

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.HillClimbing);
                        System.err.println("HC-------");
                    } else {
                        if (Executer.getInstance().getSelectedMH() == 1) {
                            EvolutionStrategies.countRef = 4;
                            EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                            EvolutionStrategies.mutationType = MutationType.GenericMutation;
                            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                            EvolutionStrategies.PM = 0.8;

                            System.err.println("EE-------RS");
                        } else
                            EvolutionStrategies.countRef = 0;

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.RandomSearch);
                        System.err.println("RS-------");
                    }


                    //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
                    //thisLapBests.add(Strategy.getStrategy().getBestState());
                    CalendarState stateAll = (CalendarState) Strategy.getStrategy().getBestState();

                    CalendarConfiguration configurationAll = stateAll.getConfiguration();

                    if (configurationAll.isSymmetricSecondRound()) {
                        deleteInauguralGame(stateAll);
                        deleteSecondRound(stateAll);
                        setSecondRound(stateAll);
                        if (configurationAll.isChampionVsSecondPlace()) {
                            if (configurationAll.isInauguralGame())
                                addInauguralGame(stateAll);
                            else
                                fixChampionSubchampion(stateAll);
                        }
                    }

                    /*Executer.getInstance().getResultStates().add(Strategy.getStrategy().getBestState());*/
                    Strategy.destroyExecute();

                    ArrayList<Date> allTogether = new ArrayList<>();

                    /*if (((Date) stateOccident.getCode().get(0)).getGames().size() == 1) {
                        stateOccident.getCode().remove(0);
                    } else if (((Date) stateOrient.getCode().get(0)).getGames().size() == 1) {
                        stateOccident.getCode().remove(0);
                    }*/

                    for (int k = 0; k < stateOccident.getCode().size(); k++) {
                        Date dateToAdd = new Date();
                        dateToAdd.getGames().addAll(((Date) stateOccident.getCode().get(k)).getGames());
                        dateToAdd.getGames().addAll(((Date) stateOrient.getCode().get(k)).getGames());
                        allTogether.add(dateToAdd);
                    }


                    for (Object object : stateAll.getCode()) {
                        Date date = (Date) object;
                        allTogether.add(date);
                    }

                    CalendarState finalState = new CalendarState();
                    finalState.getCode().addAll(allTogether);
                    CalendarConfiguration finalConfiguration = stateOccident.getConfiguration();
                    finalConfiguration.setTeamsIndexes(allTeams);
                    finalState.setConfiguration(finalConfiguration);
                    finalState.setCalendarType(stateOccident.getCalendarType());
                    Executer.getInstance().getResultStates().add(finalState);


                    updateProgress(i + 1, Executer.getInstance().getEXECUTIONS());

                    percent = percent(i + 1);
                    updateMessage(percent + " %");
                }

                updateProgress(i, Executer.getInstance().getEXECUTIONS());

                percent = percent(i);
                updateMessage(percent + " %");
                Thread.sleep(10);
                return "";
            }
        };
    }


    private int percent(int number) {
        return (number * 100) / Executer.getInstance().getEXECUTIONS();
    }

}