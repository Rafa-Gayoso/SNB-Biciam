package definition.state.statecode;

import java.util.ArrayList;

public class Date implements Cloneable {
    private ArrayList<ArrayList<Integer>> games;//Games that belongs to a Date

    /**
     * Class constructor
     */
    public Date(){
        this.games = new ArrayList<>();
    }

    /**
     * Class constructor
     *
     * @param n number of resources.teams
     */
    public Date(int n) {
        this.games = new ArrayList<>(n / 2);
    }

    /**
     * Class constructor
     *
     * @param games
     */
    public Date(ArrayList<ArrayList<Integer>> games) {
        this.games = games;
    }

    /**
     * Return the games of a Date
     *
     * @return games
     */
    public ArrayList<ArrayList<Integer>> getGames() {
        return games;
    }

    /**
     * Set the games of a Date
     *
     * @param games
     */
    public void setGames(ArrayList<ArrayList<Integer>> games) {
        this.games = games;
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public String toString() {
        return "Date{" +
                "games=" + games.toString() +
                '}';
    }
}
