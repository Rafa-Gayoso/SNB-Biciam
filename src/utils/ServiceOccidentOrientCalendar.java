package utils;

import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.concurrent.Task;
import operators.initialSolution.InitialSolutionType;
import operators.interfaces.IChampionGame;
import operators.interfaces.ICreateInitialSolution;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import problem.definition.State;

import java.util.ArrayList;

public class ServiceOccidentOrientCalendar extends javafx.concurrent.Service<String> implements ISecondRound, IInauguralGame, IChampionGame, ICreateInitialSolution {
    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {

                Executer.getInstance().configureProblem();

                int s = 0;
                updateProgress(s, Executer.getInstance().getEXECUTIONS());
                int percent = percent(s);
                updateMessage(percent + " %");
                int oldIterations = Executer.getInstance().getITERATIONS();
                Executer.getInstance().setITERATIONS(500);

                int [][] oldMatrix = new int[TTPDefinition.getInstance().getCantEquipos()][TTPDefinition.getInstance().getCantEquipos()];

                for(int i=0; i < TTPDefinition.getInstance().getDuelMatrix().length; i++){
                    for (int j = 0; j <TTPDefinition.getInstance().getDuelMatrix().length; j++) {
                        oldMatrix[i][j] = TTPDefinition.getInstance().getDuelMatrix()[i][j];
                    }
                }
                /*for (; s < Executer.getInstance().getEXECUTIONS(); s++) {*/

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

                    CalendarConfiguration originalConfiguration = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), true, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes(), TTPDefinition.getInstance().getDuelMatrix()).clone();


                    CalendarConfiguration confOnlyOccident = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOccident, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes(), matrixOnlyOccident);

                    CalendarConfiguration confOnlyOrient = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOrient, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes(), matrixOnlyOrient);

                    CalendarConfiguration confOccVsOr = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes(), newMatrix);

                    if (TTPDefinition.getInstance().getFirstPlace() != -1){
                        int posChamp = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getFirstPlace());
                        int posSub = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getSecondPlace());

                        if(posChamp != -1 && posSub != -1){
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOrient.setInauguralGame(false);
                            confOccVsOr.setChampionVsSecondPlace(false);
                        }
                        else if(posChamp == -1 && posSub == -1){
                            confOnlyOccident.setChampionVsSecondPlace(false);
                            confOnlyOccident.setInauguralGame(false);
                            confOccVsOr.setChampionVsSecondPlace(false);
                        }
                        else if(posChamp != -1 && posSub == -1 && TTPDefinition.getInstance().isInauguralGame()){
                            confOnlyOccident.setChampionVsSecondPlace(false);
                            confOnlyOccident.setInauguralGame(false);
                        }
                        else if(posChamp == -1 && posSub != -1 && TTPDefinition.getInstance().isInauguralGame()){
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOrient.setInauguralGame(false);
                        }
                        else{
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOccident.setChampionVsSecondPlace(false);
                        }
                        if (TTPDefinition.getInstance().isInauguralGame()){
                            confOccVsOr.setInauguralGame(false);
                        }
                    }

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOccident);
                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOccident);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOccident);
                    TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOccident.size()-1);
                    Executer.getInstance().executeEC();

                    ArrayList<problem.definition.State> calendarOccList = new ArrayList<>();

                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                        calendarOccList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                    }

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOrient);
                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOrient);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOrient);
                    TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOrient.size()-1);
                    Executer.getInstance().executeEC();

                    ArrayList<problem.definition.State> calendarOrList = new ArrayList<>();

                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                        calendarOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                    }

                    ArrayList<problem.definition.State> allTogetherList = new ArrayList<>();
                    ArrayList<Date> dateToStarList = new ArrayList<>();

                    if (originalConfiguration.isInauguralGame()){
                        for (int i = 0; i < calendarOccList.size(); i++) {
                            if (confOnlyOccident.isInauguralGame()){
                                deleteInauguralGame(calendarOccList.get(i));
                            }
                            else if(confOnlyOrient.isInauguralGame()){
                                deleteInauguralGame(calendarOrList.get(i));
                            }
                        }
                    }


                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {

                        problem.definition.State allTogether = new problem.definition.State();
                        for (int j = 0; j < calendarOccList.get(i).getCode().size(); j++) {

                            definition.state.statecode.Date dateOcc = new definition.state.statecode.Date();
                            dateOcc.setGames(((Date)(calendarOccList.get(i).getCode().get(j))).getGames());

                            definition.state.statecode.Date dateOr = new definition.state.statecode.Date();
                            dateOr.setGames(((Date)(calendarOrList.get(i).getCode().get(j))).getGames());

                            definition.state.statecode.Date dateTogether = new definition.state.statecode.Date();
                            dateTogether.getGames().addAll(dateOcc.getGames());
                            dateTogether.getGames().addAll(dateOr.getGames());

                            allTogether.getCode().add(dateTogether);

                            if (j == calendarOccList.get(i).getCode().size()-1){
                                dateToStarList.add(dateTogether);
                            }
                        }
                        allTogetherList.add(allTogether);
                    }
                    TTPDefinition.getInstance().getDateToStartList().addAll(dateToStarList);

                    int numberOfDate = 0;
                    if (TTPDefinition.getInstance().isSecondRound()){
                        numberOfDate = (allTeams.size()-1) - (calendarOccList.get(0).getCode().size()/2);
                    }
                    else{
                        numberOfDate = (allTeams.size()-1) - calendarOccList.get(0).getCode().size();
                    }

                    TTPDefinition.getInstance().setTeamIndexes(allTeams);
                    TTPDefinition.getInstance().setDuelMatrix(newMatrix);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOccVsOr);
                    TTPDefinition.getInstance().setNumberOfDates(numberOfDate);



                    ArrayList<Integer> originalRests = new ArrayList<>();
                    originalRests.addAll(TTPDefinition.getInstance().getRestIndexes());

                    int numberToModifyRests = allTogetherList.get(0).getCode().size();
                    if (TTPDefinition.getInstance().isInauguralGame()){
                        numberToModifyRests += 1;
                    }

                    boolean setDateToStart = true;

                    ArrayList<Integer> modifiedRests = new ArrayList<>();
                    for (int i = 0; i < originalRests.size(); i++) {
                        if (originalRests.get(i) > numberToModifyRests){
                            int tempRest = originalRests.get(i);
                            tempRest -= numberToModifyRests;
                            modifiedRests.add(tempRest);
                        }
                        else if (originalRests.get(i) == numberToModifyRests){
                            setDateToStart = false;
                        }
                    }
                    TTPDefinition.getInstance().setRestIndexes(modifiedRests);

                    if (setDateToStart){
                        Executer.getInstance().setTimeToSetDateToStart(true);
                        TTPDefinition.getInstance().setUseDateToStart(true);
                    }

                    Executer.getInstance().executeEC();

                    TTPDefinition.getInstance().setRestIndexes(originalRests);

                    if (setDateToStart){
                        Executer.getInstance().setTimeToSetDateToStart(false);
                        TTPDefinition.getInstance().setUseDateToStart(false);
                    }

                    ArrayList<problem.definition.State> calendarOccVsOrList = new ArrayList<>();

                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                        calendarOccVsOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                    }

                    if (originalConfiguration.isInauguralGame() && confOccVsOr.isInauguralGame()){
                        for (int i = 0; i < calendarOccVsOrList.size(); i++) {
                            deleteInauguralGame(calendarOccVsOrList.get(i));
                        }
                    }

                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                        allTogetherList.get(i).getCode().addAll(calendarOccVsOrList.get(i).getCode());
                    }

                    InitialSolutionType type = createSolutionType();
                    for(int j =0; j < Executer.getInstance().getEXECUTIONS();j++){
                    updateProgress(s + 1, Executer.getInstance().getEXECUTIONS());

                    percent = percent(s + 1);
                    updateMessage(percent + " %");
                    s++;
                        Thread.sleep(10);
                    }


                updateProgress(s, Executer.getInstance().getEXECUTIONS());
                    for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                        CalendarState tempState = new CalendarState();
                        tempState.getCode().addAll(allTogetherList.get(i).getCode());
                        CalendarConfiguration tempConfiguration = new CalendarConfiguration(originalConfiguration.getCalendarId(),
                                allTeams, originalConfiguration.isInauguralGame(), originalConfiguration.isChampionVsSecondPlace(),
                                originalConfiguration.getChampion(), originalConfiguration.getSecondPlace(), originalConfiguration.isSecondRoundCalendar(),
                                originalConfiguration.isSymmetricSecondRound(), originalConfiguration.isOccidenteVsOriente(), originalConfiguration.getMaxLocalGamesInARow(),
                                originalConfiguration.getMaxVisitorGamesInARow(), originalConfiguration.getRestDates());

                        tempState.setConfiguration(tempConfiguration);
                        tempState.setCalendarType(type.ordinal());
                        if (originalConfiguration.isInauguralGame()){
                            addInauguralGame(tempState);
                        }
                        tempState.getConfiguration().setDuelMatrix(oldMatrix);

                        Executer.getInstance().getResultStates().add(tempState);

                        if( Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()) == null){
                            Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(), 1);
                        }else{
                            ;
                            Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(),
                                    Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId())+1);

                        }
                        tempState.getConfiguration().setCalendarId(TTPDefinition.getInstance().getCalendarId() +"."+
                                Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()));

                        if( Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId()) == null){
                            Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(), 1);
                        }else{
                            Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(),
                                    Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId())+1);
                        }
                    }

                   /*updateProgress(s + 1, Executer.getInstance().getEXECUTIONS());

                    percent = percent(s + 1);
                    updateMessage(percent + " %");*/


                //updateProgress(s, Executer.getInstance().getEXECUTIONS());

                /*percent = percent(s);
                updateMessage(percent + " %");*/
                Thread.sleep(10);
                Executer.getInstance().setITERATIONS(oldIterations);
                return "";
            }
        };
    }


    private int percent(int number) {
        return (number * 100) / Executer.getInstance().getEXECUTIONS();
    }

}