package operators.mutation;

import com.sun.prism.shader.Solid_Color_AlphaTest_Loader;
import definition.state.statecode.Date;
import problem.definition.State;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeDuelOperator extends MutationOperator {
    @Override
    public State applyMutation(State state) {
        State resultSate = new State();
        copyState(resultSate,state);
        int posFirstDate = -1;
        int posLastDate = -1;
        int posFirstDuel = -1;
        int startPosition = 0;

        /*if (!mutationsConfigurationsList.isEmpty()) {
            posFirstDate = mutationsConfigurationsList.get(number).get(0);
            posLastDate = mutationsConfigurationsList.get(number).get(1);
            posFirstDuel = mutationsConfigurationsList.get(number).get(2);

        }*/

        if (posFirstDate == -1) {
            do {
                posFirstDate = ThreadLocalRandom.current().nextInt(startPosition, resultSate.getCode().size());
            }
            while (posLastDate == posFirstDate);
        }

        if (posLastDate == -1) {
            do {
                posLastDate = ThreadLocalRandom.current().nextInt(startPosition, resultSate.getCode().size());
            }
            while (posLastDate == posFirstDate);
        }

        Date firstDate = (Date)resultSate.getCode().get(posFirstDate);
        Date secondDate = (Date)resultSate.getCode().get(posLastDate);

        if (posFirstDuel == -1) {
            do{
                posFirstDuel = ThreadLocalRandom.current().nextInt(0, firstDate.getGames().size());
            }
            while (secondDate.getGames().contains(firstDate.getGames().get(posFirstDuel)));
        }


        swapTeams(posFirstDuel, false, firstDate, secondDate);
        return resultSate ;
    }

    private void swapTeams(int posGame, boolean compatible, Date firstDate, Date secondDate) {
        ArrayList<Integer> firstDuel = firstDate.getGames().get(posGame);
        int tempSize = secondDate.getGames().size();
        secondDate.getGames().add(firstDuel);
        firstDate.getGames().remove(firstDuel);
        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        results.add(firstDuel);
        while (!compatible) {
            results = incompatibleDuels(secondDate, results, tempSize);
            if (results.isEmpty()) {
                compatible = true;
            } else {
                tempSize = firstDate.getGames().size();
                firstDate.getGames().addAll(results);

                for (ArrayList<Integer> duel: results) {
                    secondDate.getGames().remove(secondDate.getGames().indexOf(duel));
                }

                results = incompatibleDuels(firstDate, results, tempSize);
                if (results.isEmpty()) {
                    compatible = true;
                } else {
                    tempSize = secondDate.getGames().size();
                    secondDate.getGames().addAll(results);
                    firstDate.getGames().removeAll(results);
                }
            }
        }
    }

    private ArrayList<ArrayList<Integer>> incompatibleDuels(Date date, ArrayList<ArrayList<Integer>> duels, int size) {
        ArrayList<ArrayList<Integer>> incompatibleDuels = new ArrayList<>();

        for (ArrayList<Integer> duel : duels) {
            for (int j = 0; j < size; j++) {
                ArrayList<Integer> dateDuels = date.getGames().get(j);

                if (dateDuels.contains(duel.get(0)) || dateDuels.contains(duel.get(1))) {
                    if (!incompatibleDuels.contains(dateDuels)) {
                        incompatibleDuels.add(dateDuels);
                    }
                }
            }
        }
        return incompatibleDuels;
    }
}
