package com.hagenberg.jarvis;

import com.hagenberg.jarvis.views.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Jarvis extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        mainView.show(primaryStage);
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
