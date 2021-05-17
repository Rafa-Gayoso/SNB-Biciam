package definition.state;

import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;

public class CalendarState extends State {
    
    private int calendarType;
    private CalendarConfiguration configuration;


    @Override
    public State clone() {
        State state = new CalendarState();
        try {
        for (Object object : this.getCode()) {
            try {
                state.getCode().add(((Date)(object)).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

            ((CalendarState)state).setConfiguration(this.getConfiguration().clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return state;
    }


    public int getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(int calendarType) {
        this.calendarType = calendarType;
    }

    public CalendarConfiguration getConfiguration(){
        return this.configuration;
    }

    public void setConfiguration(CalendarConfiguration configuration) {
        this.configuration = configuration;
    }
}
