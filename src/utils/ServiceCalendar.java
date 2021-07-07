package utils;

import definition.TTPDefinition;
import definition.state.CalendarState;
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


public class ServiceCalendar extends javafx.concurrent.Service<String> implements ISecondRound, IInauguralGame, IChampionGame {

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                Executer.getInstance().configureProblem();



                int i=0;
                updateProgress(i,Executer.getInstance().getEXECUTIONS());
                int percent = percent(i);
                updateMessage(percent+" %");
                //TTPDefinition.getInstance().setOccidentOrientConfiguration(null);
                for (; i < Executer.getInstance().getEXECUTIONS(); i++) {

                    if(Executer.getInstance().isTimeToSetDateToStart()){
                        TTPDefinition.getInstance().setDateToStart(TTPDefinition.getInstance().getDateToStartList().get(i));
                    }

                    Strategy.getStrategy().setStopexecute(new StopExecute());
                    Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                    Strategy.getStrategy().setProblem(Executer.getInstance().getProblem());
                    Strategy.getStrategy().saveListBestStates = true;
                    Strategy.getStrategy().saveListStates = true;
                    Strategy.getStrategy().calculateTime = true;
                    if(Executer.getInstance().getSelectedMH() == 0){

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.HillClimbing);
                        System.err.println("HC-------");
                    }
                    else{
                        if(Executer.getInstance().getSelectedMH() == 1){
                            EvolutionStrategies.countRef = 4;
                            EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                            EvolutionStrategies.mutationType = MutationType.GenericMutation;
                            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                            EvolutionStrategies.PM = 0.8;

                            System.err.println("EE-------RS");
                        }
                        else
                            EvolutionStrategies.countRef = 0;

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(),1, GeneratorType.RandomSearch);
                        System.err.println("RS-------");
                    }



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

                    Executer.getInstance().getResultStates().add(state);

                    Strategy.destroyExecute();
                    /*Strategy.getStrategy().setStopexecute(new StopExecute());
                    Strategy.getStrategy().setUpdateparameter(new UpdateParameter());
                    Strategy.getStrategy().setProblem(Executer.getInstance().getProblem());
                    Strategy.getStrategy().saveListBestStates = true;
                    Strategy.getStrategy().saveListStates = true;
                    Strategy.getStrategy().calculateTime = true;
                    if(Executer.getInstance().getSelectedMH() == 0){

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(), 1, GeneratorType.HillClimbing);
                        System.err.println("HC-------");
                    }
                    else{
                        if(Executer.getInstance().getSelectedMH() == 1){
                            EvolutionStrategies.countRef = 4;
                            EvolutionStrategies.selectionType = SelectionType.RouletteSelection;
                            EvolutionStrategies.mutationType = MutationType.GenericMutation;
                            EvolutionStrategies.replaceType = ReplaceType.GenerationalReplace;
                            EvolutionStrategies.PM = 0.8;

                            System.err.println("EE-------RS");
                        }
                        else
                            EvolutionStrategies.countRef = 0;

                        Strategy.getStrategy().executeStrategy(Executer.getInstance().getITERATIONS(),1, GeneratorType.RandomSearch);
                        System.err.println("RS-------");
                    }


                    //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
                    //thisLapBests.add(Strategy.getStrategy().getBestState());
                    CalendarState state = (CalendarState) Strategy.getStrategy().getBestState();

                    CalendarConfiguration configuration = state.getConfiguration();

                    if(configuration.isSymmetricSecondRound()){
                        deleteInauguralGame(state);
                        deleteSecondRound(state);
                        setSecondRound(state);
                        if (configuration.isChampionVsSecondPlace()) {
                            if (configuration.isInauguralGame())
                                addInauguralGame(state);
                            else
                                fixChampionSubchampion(state);
                        }
                    }
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

                    Executer.getInstance().getResultStates().add(state);
                    Strategy.destroyExecute();*/

                    updateProgress(i+1,Executer.getInstance().getEXECUTIONS());

                    percent = percent(i+1);
                    updateMessage(percent+" %");
                }



                updateProgress(i,Executer.getInstance().getEXECUTIONS());

                percent = percent(i);
                updateMessage(percent+" %");
                Thread.sleep(10);
                return "";
            }
        };
    }




    private int percent(int number){
        return (number*100)/Executer.getInstance().getEXECUTIONS();
    }
}