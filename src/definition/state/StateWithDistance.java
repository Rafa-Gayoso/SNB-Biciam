package definition.state;


import definition.TTPDefinition;
import definition.state.statecode.Date;
import problem.definition.State;
import utils.Distance;

import java.util.ArrayList;

public class StateWithDistance extends State {

    private double distance;

    public StateWithDistance(State ps) {
        this.code = ps.getCode();
        this.distance = Distance.getInstance().calculateCalendarDistance(ps);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


}
