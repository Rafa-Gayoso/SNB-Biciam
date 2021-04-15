package utils;

public class TeamsPairDistance {
    private int firstTeam;//Position of the local team
    private int secondTeam;//Position of the visitor team
    private double distance;//Distance between

    /**
     * Class Constructor
     *
     * @param firstTeam   local team position
     * @param secondTeam visitor team position
     * @param distance   distance between stadiums
     */
    public TeamsPairDistance(int firstTeam, int secondTeam, double distance) {
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.distance = distance;
    }

    /**
     * Method that return the position of the local team
     *
     * @return local team position
     */
    public int getFirstTeam() {
        return firstTeam;
    }

    /**
     * Method that set the position of the local team
     *
     * @param firstTeam
     */
    public void setFirstTeam(int firstTeam) {
        this.firstTeam = firstTeam;
    }

    /**
     * Method that return the position of the visitor team
     *
     * @return visitor team position
     */
    public int getSecondTeam() {
        return secondTeam;
    }

    /**
     * Method that set the position of the local team
     *
     * @param secondTeam
     */
    public void setSecondTeam(int secondTeam) {
        this.secondTeam = secondTeam;
    }

    /**
     * Method that return the distance between stadiums
     *
     * @return distance
     */

    public double getDistance() {
        return distance;
    }

    /**
     * Method that set the distance between stadiums
     *
     * @param distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

}
