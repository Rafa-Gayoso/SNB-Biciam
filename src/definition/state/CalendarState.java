package definition.state;

import definition.state.statecode.Date;
import problem.definition.State;
import utils.CalendarConfiguration;
import utils.Distance;

public class CalendarState extends State {
    
    private int calendarType;
    private CalendarConfiguration configuration;
    private double distance;

    public CalendarState(State ps, CalendarConfiguration configuration) {
        this.code = ps.getCode();
        this.configuration = configuration;
        //this.distance = Distance.getInstance().calculateCalendarDistance(ps);
    }

    public CalendarState() {

    }

    @Override
    public State clone() {
        State cloneState = new CalendarState();
        try {
            for (Object object : this.getCode()) {
                try {

                    Date date = (Date) object;
                    Date cloneDate = date.clone();
                    cloneState.getCode().add(cloneDate);

                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            CalendarConfiguration configurationClone = this.getConfiguration().clone();
            ((CalendarState)cloneState).setConfiguration(configurationClone);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloneState;
    }

    public double getDistance() {
        distance = Distance.getInstance().calculateCalendarDistance(this);
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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
