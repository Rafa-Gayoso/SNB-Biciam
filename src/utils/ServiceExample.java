package utils;

import execute.Executer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Service extends Service<String> {

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                Executer.getInstance().executeEC();
                Thread.sleep(5000);
                return "";
            }
        };
    }
}