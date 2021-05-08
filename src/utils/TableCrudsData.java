package utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableCrudsData {

     private SimpleStringProperty name;
     private SimpleStringProperty acronym;
     private SimpleStringProperty location;
     private SimpleObjectProperty distance;

    public TableCrudsData(String name, String acronym, String location, Object distance){
        this.name = new SimpleStringProperty(name);
        this.acronym = new SimpleStringProperty(acronym);
        this.location = new SimpleStringProperty(location);
        this.distance = new SimpleObjectProperty(distance);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAcronym() {
        return acronym.get();
    }

    public SimpleStringProperty acronymProperty() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym.set(acronym);
    }

    public Object getDistance() {
        return distance.get();
    }

    public SimpleObjectProperty distanceProperty() {
        return distance;
    }

    public void setDistance(Object distance) {
        this.distance.set(distance);
    }

    public String getLocation() { return location.get(); }

    public SimpleStringProperty locationProperty() { return location; }

    public void setLocation(String location) { this.location.set(location); }
}
