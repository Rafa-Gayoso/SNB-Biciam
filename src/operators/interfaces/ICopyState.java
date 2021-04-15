package operators.interfaces;

import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;

public interface ICopyState {

    default void copyState(State newCopy, State copy) {
        for (int i =0; i < copy.getCode().size();i++){
            Date value = (Date)copy.getCode().get(i);
            Date date = new Date(null);
            ArrayList<ArrayList<Integer>> games = new ArrayList<>();
            for (int j = 0; j < value.getGames().size(); j++) {
                ArrayList<Integer> game = new ArrayList<>();
                game.add(value.getGames().get(j).get(0));
                game.add(value.getGames().get(j).get(1));
                games.add(game);
            }
            date.setGames(games);
            newCopy.getCode().add(date);
        }
    }
}
