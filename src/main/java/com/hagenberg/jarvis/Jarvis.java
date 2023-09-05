package com.hagenberg.jarvis;

import com.hagenberg.jarvis.debuggee.JDIExampleDebuggee;
import com.hagenberg.jarvis.debugger.JarvisDebuggerService;
import com.hagenberg.jarvis.util.ServiceProvider;
import com.hagenberg.jarvis.views.MainView;
import com.tangorabox.componentinspector.fx.FXComponentInspectorHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class Jarvis extends Application {
    @Override
    public void start(Stage primaryStage) {
        JarvisDebuggerService debugger = new JarvisDebuggerService(JDIExampleDebuggee.class);
        int[] breakPoints = {27};
        debugger.setBreakPointLines(breakPoints);
        debugger.launch();
        ServiceProvider.getInstance().registerDependency(JarvisDebuggerService.class, debugger);

        MainView mainView = new MainView();
        mainView.show(primaryStage);
        FXComponentInspectorHandler.handleAll();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
