package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class responsible for representations of the duel between two resources.teams
 *
 * @visitor Rafael Gayoso and Mario Herrera
 */
public class Duel {

   private SimpleStringProperty local;
   private SimpleStringProperty visitor;

    /**
     * Class constructor
     *
     * @param local   name of the local team
     * @param visitor name of the visitor team
     */
    public Duel(String local, String visitor) {
        this.local = new SimpleStringProperty(local);
        this.visitor = new SimpleStringProperty(visitor);
    }

    @Override
    public String toString() {

        return (local.get() + " vs " + visitor.get());
    }

    public void setLocal(String value) { localProperty().set(value); }
    public String getLocal() { return localProperty().get(); }
    public StringProperty localProperty() {
        if (local == null) local = new SimpleStringProperty(this, "firstName");
        return local;
    }

    public void setVisitor(String value) { visitorProperty().set(value); }
    public String getVisitor() { return visitorProperty().get(); }
    public StringProperty visitorProperty() {
        if (visitor == null) visitor = new SimpleStringProperty(this, "lastName");
        return visitor;
    }
}
