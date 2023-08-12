package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.util.SVGManager;
import javafx.scene.layout.VBox;

public class WindowMenu extends VBox {
    private final boolean bottomUp;

    public WindowMenu() {
        super();
        this.getStyleClass().add("window-menu");
        this.bottomUp = false;
    }

    public WindowMenu(boolean bottomUp) {
        super();
        this.getStyleClass().add("window-menu");
        this.bottomUp = bottomUp;
    }

    public void addWindow(AuxiliaryPane pane, String iconPath) {
        getChildren().add(new WindowMenuItem(pane, SVGManager.getInstance().getSVG(iconPath), bottomUp));
    }
}
