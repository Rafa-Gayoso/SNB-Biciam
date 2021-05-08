package definition.state;

import definition.state.statecode.Date;
import problem.definition.State;

public class CalendarState extends State {
    
    private int calendarType;


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


    public int getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(int calendarType) {
        this.calendarType = calendarType;
    }


}
