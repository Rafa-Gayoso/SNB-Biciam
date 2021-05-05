package definition.state;

import definition.state.statecode.Date;
import problem.definition.State;

public class CalendarState extends State {

    @Override
    public State clone() {
        State state = new CalendarState();
        for (Object object : this.getCode()) {
            try {
                state.getCode().add(((Date)(object)).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return state;
    }
}
