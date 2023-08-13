package com.hagenberg.jarvis;

import com.hagenberg.jarvis.debuggee.JDIExampleDebuggee;
import com.hagenberg.jarvis.debugger.JarvisDebuggerService;
import com.hagenberg.jarvis.util.ServiceProvider;
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
        JarvisDebuggerService debugger = new JarvisDebuggerService(JDIExampleDebuggee.class);
        int[] breakPoints = {8};
        debugger.setBreakPointLines(breakPoints);
        debugger.launch();
        ServiceProvider.getInstance().registerDependency(JarvisDebuggerService.class, debugger);
    }
}
