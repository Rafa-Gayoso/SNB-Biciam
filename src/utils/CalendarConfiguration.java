package utils;

import java.util.ArrayList;

public class CalendarConfiguration {

    private String calendarId;
    private ArrayList<Integer> teamsIndexes;
    private boolean inauguralGame;
    private boolean championVsSecondPlace;
    private int champion;
    private int secondPlace;
    private boolean secondRoundCalendar;
    private boolean symmetricSecondRound;
    private boolean OccidenteVsOriente;
    private int maxLocalGamesInARow;
    private int maxVisitorGamesInARow;
    private ArrayList<Integer> restDates;

    public CalendarConfiguration(String calendarId, ArrayList<Integer> teamsIndexes, boolean inauguralGame,
                                 boolean championVsSecondPlace, int champion, int secondPlace,
                                 boolean secondRoundCalendar, boolean symmetricSecondRound, boolean OccidenteVsOriente,
                                 int maxLocalGamesInARow, int maxVisitorGamesInARow, ArrayList<Integer> restDates) {
        this.calendarId = calendarId;
        this.teamsIndexes = teamsIndexes;
        this.inauguralGame = inauguralGame;
        this.championVsSecondPlace = championVsSecondPlace;
        this.champion = champion;
        this.secondPlace = secondPlace;
        this.secondRoundCalendar = secondRoundCalendar;
        this.symmetricSecondRound = symmetricSecondRound;
        this.OccidenteVsOriente = OccidenteVsOriente;
        this.maxLocalGamesInARow = maxLocalGamesInARow;
        this.maxVisitorGamesInARow = maxVisitorGamesInARow;
        this.restDates = restDates;
    }

    public CalendarConfiguration() {
        this.calendarId = "";
        this.teamsIndexes = new ArrayList<>();
        this.inauguralGame = false;
        this.championVsSecondPlace = false;
        this.champion = -1;
        this.secondPlace = -1;
        this.secondRoundCalendar = false;
        this.symmetricSecondRound = false;
        this.OccidenteVsOriente = false;
        this.maxLocalGamesInARow = 0;
        this.maxVisitorGamesInARow = 0;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public ArrayList<Integer> getTeamsIndexes() {
        return teamsIndexes;
    }

    public void setTeamsIndexes(ArrayList<Integer> teamsIndexes) {
        this.teamsIndexes = teamsIndexes;
    }

    public boolean isInauguralGame() {
        return inauguralGame;
    }

    public void setInauguralGame(boolean inauguralGame) {
        this.inauguralGame = inauguralGame;
    }

    public boolean isChampionVsSecondPlace() {
        return championVsSecondPlace;
    }

    public void setChampionVsSecondPlace(boolean championVsSecondPlace) {
        this.championVsSecondPlace = championVsSecondPlace;
    }

    public int getChampion() {
        return champion;
    }

    public void setChampion(int champion) {
        this.champion = champion;
    }

    public int getSecondPlace() {
        return secondPlace;
    }

    public void setSecondPlace(int secondPlace) {
        this.secondPlace = secondPlace;
    }

    public boolean isSecondRoundCalendar() {
        return secondRoundCalendar;
    }

    public void setSecondRoundCalendar(boolean secondRoundCalendar) {
        this.secondRoundCalendar = secondRoundCalendar;
    }

    public boolean isSymmetricSecondRound() {
        return symmetricSecondRound;
    }

    public void setSymmetricSecondRound(boolean symmetricSecondRound) {
        this.symmetricSecondRound = symmetricSecondRound;
    }

    public int getMaxLocalGamesInARow() {
        return maxLocalGamesInARow;
    }

    public void setMaxLocalGamesInARow(int maxLocalGamesInARow) {
        this.maxLocalGamesInARow = maxLocalGamesInARow;
    }

    public int getMaxVisitorGamesInARow() {
        return maxVisitorGamesInARow;
    }

    public void setMaxVisitorGamesInARow(int maxVisitorGamesInARow) {
        this.maxVisitorGamesInARow = maxVisitorGamesInARow;
    }

    public boolean isOccidenteVsOriente() { return OccidenteVsOriente; }

    public void setOccidenteVsOriente(boolean occidenteVsOriente) { OccidenteVsOriente = occidenteVsOriente; }

    @Override
    public String toString() {
        return "CalendarConfiguration{" +
                "calendarId='" + calendarId + '\'' +
                ", teamsIndexes=" + teamsIndexes +
                ", inauguralGame=" + inauguralGame +
                ", championVsSecondPlace=" + championVsSecondPlace +
                ", champion=" + champion +
                ", secondPlace=" + secondPlace +
                ", secondRoundCalendar=" + secondRoundCalendar +
                ", symmetricSecondRound=" + symmetricSecondRound +
                ", maxLocalGamesInARow=" + maxLocalGamesInARow +
                ", maxVisitorGamesInARow=" + maxVisitorGamesInARow +
                ", restDates=" + restDates +
                '}';
    }

    @Override
    public CalendarConfiguration clone() throws CloneNotSupportedException {
        CalendarConfiguration clone = new CalendarConfiguration();
        clone.setCalendarId(calendarId);
        clone.setTeamsIndexes((ArrayList<Integer>) this.teamsIndexes.clone());
        clone.setInauguralGame(this.inauguralGame);
        clone.setChampionVsSecondPlace(this.championVsSecondPlace);
        clone.setChampion(this.champion);
        clone.setSecondPlace(this.secondPlace);
        clone.setSecondRoundCalendar(this.secondRoundCalendar);
        clone.setSymmetricSecondRound(this.symmetricSecondRound);
        clone.setOccidenteVsOriente(this.OccidenteVsOriente);
        clone.setMaxLocalGamesInARow(this.maxLocalGamesInARow);
        clone.setMaxVisitorGamesInARow(this.maxVisitorGamesInARow);
        clone.setRestDates(this.restDates);
        return clone;
    }

    public ArrayList<Integer> getRestDates() {
        return restDates;
    }

    public void setRestDates(ArrayList<Integer> restDates) {
        this.restDates = restDates;
    }
}
