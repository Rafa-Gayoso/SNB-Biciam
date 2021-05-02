package operators.interfaces;

import definition.TTPDefinition;
import definition.state.statecode.Date;
import problem.definition.State;

public interface IChampionGame extends ISwapTeams{

    default void fixChampionSubchampion(State state) {

        boolean found = false;
        int posDate = -1;
        int posGame = -1;
        int i = 0;

        int posChampion = TTPDefinition.getInstance().getFirstPlace();
        int posSubChampion = TTPDefinition.getInstance().getSecondPlace();

        while (i <state.getCode().size() && !found) {
            Date date = (Date) state.getCode().get(i);
            int j = 0;
            while (j < date.getGames().size() && !found) {
                if (date.getGames().get(j).get(0) == posChampion && date.getGames().get(j).get(0) == posSubChampion) {
                    found = true;
                    posDate = i;
                    posGame = j;
                } else
                    j++;
            }
            i++;
        }

        if ((posDate != -1) && (posDate != 0) ) {
            Date firstDate = (Date) state.getCode().get(posDate);
            Date secondDate = (Date) state.getCode().get(0);

            swapTeams(posGame, false, firstDate, secondDate);
        }
    }
}
