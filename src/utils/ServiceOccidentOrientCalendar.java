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
import java.util.ArrayList;

public class ServiceOccidentOrientCalendar extends javafx.concurrent.Service<String> implements ISecondRound, IInauguralGame, IChampionGame, ICreateInitialSolution {
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

                    CalendarConfiguration originalConfiguration = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), true, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(), TTPDefinition.getInstance().getRestIndexes());


                    CalendarConfiguration confOnlyOccident = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOccident, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(), TTPDefinition.getInstance().getRestIndexes());

                    CalendarConfiguration confOnlyOrient = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            teamsOnlyOrient, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(), TTPDefinition.getInstance().getRestIndexes());

                    CalendarConfiguration confOccVsOr = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                            allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                            TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                            TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                            TTPDefinition.getInstance().getCantVecesVisitante(), TTPDefinition.getInstance().getRestIndexes());

                    if (TTPDefinition.getInstance().getFirstPlace() != -1) {
                        int posChamp = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getFirstPlace());
                        int posSub = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getSecondPlace());

                        if (posChamp != -1 && posSub != -1) {
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOrient.setInauguralGame(false);
                            confOccVsOr.setChampionVsSecondPlace(false);
                        } else if (posChamp == -1 && posSub == -1) {
                            confOnlyOccident.setChampionVsSecondPlace(false);
                            confOnlyOccident.setInauguralGame(false);
                            confOccVsOr.setChampionVsSecondPlace(false);
                        } else if (posChamp != -1 && posSub == -1 && TTPDefinition.getInstance().isInauguralGame()) {
                            confOnlyOccident.setChampionVsSecondPlace(false);
                            confOnlyOccident.setInauguralGame(false);
                        } else if (posChamp == -1 && posSub != -1 && TTPDefinition.getInstance().isInauguralGame()) {
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOrient.setInauguralGame(false);
                        } else {
                            confOnlyOrient.setChampionVsSecondPlace(false);
                            confOnlyOccident.setChampionVsSecondPlace(false);
                        }
                        if (TTPDefinition.getInstance().isInauguralGame()) {
                            confOccVsOr.setInauguralGame(false);
                        }
                    }

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOccident);
                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOccident);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOccident);
                    TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOccident.size() - 1);
                    Executer.getInstance().executeEC();

                    ArrayList<problem.definition.State> calendarOccList = new ArrayList<>();

                    for (int j = 0; j < Executer.getInstance().getEXECUTIONS(); j++) {
                        calendarOccList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size() - 1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size() - 1);
                    }

                    TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOrient);
                    TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOrient);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOrient);
                    TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOrient.size() - 1);
                    Executer.getInstance().executeEC();

                    ArrayList<problem.definition.State> calendarOrList = new ArrayList<>();

                    for (int j = 0; j < Executer.getInstance().getEXECUTIONS(); j++) {
                        calendarOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size() - 1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size() - 1);
                    }

                    ArrayList<problem.definition.State> allTogetherList = new ArrayList<>();
                    ArrayList<Date> dateToStarList = new ArrayList<>();

                    if (originalConfiguration.isInauguralGame()) {
                        for (int j = 0; j < calendarOccList.size(); j++) {
                            if (confOnlyOccident.isInauguralGame()) {
                                deleteInauguralGame(calendarOccList.get(j));
                            } else if (confOnlyOrient.isInauguralGame()) {
                                deleteInauguralGame(calendarOrList.get(j));
                            }
                        }
                    }


                    for (int k = 0; k < Executer.getInstance().getEXECUTIONS(); k++) {

                        problem.definition.State allTogether = new problem.definition.State();
                        for (int j = 0; j < calendarOccList.get(i).getCode().size(); j++) {

                            Date dateOcc = new definition.state.statecode.Date();
                            dateOcc.setGames(((Date) (calendarOccList.get(k).getCode().get(j))).getGames());

                            Date dateOr = new definition.state.statecode.Date();
                            dateOr.setGames(((Date) (calendarOrList.get(k).getCode().get(j))).getGames());

                            Date dateTogether = new definition.state.statecode.Date();
                            dateTogether.getGames().addAll(dateOcc.getGames());
                            dateTogether.getGames().addAll(dateOr.getGames());

                            allTogether.getCode().add(dateTogether);

                            if (j == calendarOccList.get(k).getCode().size() - 1) {
                                dateToStarList.add(dateTogether);
                            }
                        }
                        allTogetherList.add(allTogether);
                    }
                    TTPDefinition.getInstance().getDateToStartList().addAll(dateToStarList);

                    int numberOfDate = 0;
                    if (TTPDefinition.getInstance().isSecondRound()) {
                        numberOfDate = (allTeams.size() - 1) - (calendarOccList.get(0).getCode().size() / 2);
                    } else {
                        numberOfDate = (allTeams.size() - 1) - calendarOccList.get(0).getCode().size();
                    }

                    TTPDefinition.getInstance().setTeamIndexes(allTeams);
                    TTPDefinition.getInstance().setDuelMatrix(newMatrix);
                    TTPDefinition.getInstance().setOccidentOrientConfiguration(confOccVsOr);
                    TTPDefinition.getInstance().setNumberOfDates(numberOfDate);


                    ArrayList<Integer> originalRests = new ArrayList<>();
                    originalRests.addAll(TTPDefinition.getInstance().getRestIndexes());

                    int numberToModifyRests = allTogetherList.get(0).getCode().size();
                    if (TTPDefinition.getInstance().isInauguralGame()) {
                        numberToModifyRests += 1;
                    }

                    boolean setDateToStart = true;

                    ArrayList<Integer> modifiedRests = new ArrayList<>();
                    for (int j = 0;j < originalRests.size(); j++) {
                        if (originalRests.get(j) > numberToModifyRests) {
                            int tempRest = originalRests.get(j);
                            tempRest -= numberToModifyRests;
                            modifiedRests.add(tempRest);
                        } else if (originalRests.get(j) == numberToModifyRests) {
                            setDateToStart = false;
                        }
                    }
                    TTPDefinition.getInstance().setRestIndexes(modifiedRests);

                    if (setDateToStart) {
                        Executer.getInstance().setTimeToSetDateToStart(true);
                        TTPDefinition.getInstance().setUseDateToStart(true);
                    }

                    Executer.getInstance().executeEC();

                    TTPDefinition.getInstance().setRestIndexes(originalRests);

                    if (setDateToStart) {
                        Executer.getInstance().setTimeToSetDateToStart(false);
                        TTPDefinition.getInstance().setUseDateToStart(false);
                    }

                    ArrayList<problem.definition.State> calendarOccVsOrList = new ArrayList<>();

                    for (int j= 0; j < Executer.getInstance().getEXECUTIONS(); j++) {
                        calendarOccVsOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size() - 1));
                        Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size() - 1);
                    }

                    if (originalConfiguration.isInauguralGame() && confOccVsOr.isInauguralGame()) {
                        for (int j = 0; j < calendarOccVsOrList.size(); j++) {
                            deleteInauguralGame(calendarOccVsOrList.get(j));
                        }
                    }

                    for (int j = 0; j < Executer.getInstance().getEXECUTIONS(); j++) {
                        allTogetherList.get(j).getCode().addAll(calendarOccVsOrList.get(j).getCode());
                    }

                    InitialSolutionType type = createSolutionType();

                    for (int j = 0; j < Executer.getInstance().getEXECUTIONS(); j++) {
                        CalendarState tempState = new CalendarState();
                        tempState.getCode().addAll(allTogetherList.get(j).getCode());
                        CalendarConfiguration tempConfiguration = new CalendarConfiguration(originalConfiguration.getCalendarId(),
                                allTeams, originalConfiguration.isInauguralGame(), originalConfiguration.isChampionVsSecondPlace(),
                                originalConfiguration.getChampion(), originalConfiguration.getSecondPlace(), originalConfiguration.isSecondRoundCalendar(),
                                originalConfiguration.isSymmetricSecondRound(), originalConfiguration.isOccidenteVsOriente(), originalConfiguration.getMaxLocalGamesInARow(),
                                originalConfiguration.getMaxVisitorGamesInARow(), originalConfiguration.getRestDates());

                        tempState.setConfiguration(tempConfiguration);
                        tempState.setCalendarType(type.ordinal());
                        if (originalConfiguration.isInauguralGame()) {
                            addInauguralGame(tempState);
                        }
                        Executer.getInstance().getResultStates().add(tempState);

                        if (Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()) == null) {
                            Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(), 1);
                        } else {
                            ;
                            Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(),
                                    Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()) + 1);

                        }
                        tempState.getConfiguration().setCalendarId(TTPDefinition.getInstance().getCalendarId() + "." +
                                Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()));

                        if (Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId()) == null) {
                            Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(), 1);
                        } else {
                            Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(),
                                    Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId()) + 1);
                        }
                    }

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