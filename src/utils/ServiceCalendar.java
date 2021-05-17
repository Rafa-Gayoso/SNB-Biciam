package utils;

import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.ReplaceType;
import evolutionary_algorithms.complement.SelectionType;
import execute.Executer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import local_search.complement.StopExecute;
import local_search.complement.UpdateParameter;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.EvolutionStrategies;
import metaheuristics.generators.GeneratorType;

public class ServiceCalendar extends javafx.concurrent.Service<String> {

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
                for (; i < Executer.getInstance().getEXECUTIONS(); i++) {
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


                    //createCalendarSheet(workbook,Strategy.getStrategy().getBestState(),i);
                    //thisLapBests.add(Strategy.getStrategy().getBestState());
                    Executer.getInstance().getResultStates().add(Strategy.getStrategy().getBestState());
                    Strategy.destroyExecute();
                    updateProgress(i,Executer.getInstance().getEXECUTIONS());

                    percent = percent(i);
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