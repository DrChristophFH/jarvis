package com.hagenberg.jarvis.util;

import javafx.scene.shape.SVGPath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SVGManager {
    private static SVGManager instance;
    private final Map<String, String> cache = new HashMap<>();

    private SVGManager() {
    }

    public static SVGManager getInstance() {
        if (instance == null) {
            instance = new SVGManager();
        }
        return instance;
    }

    public SVGPath getSVG(String resourceName) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(
                cache.computeIfAbsent(resourceName, this::getPathFromResource)
        );
        return svgPath;
    }

    private String getPathFromResource(String resourceName) {
        InputStream inputStream = getClass().getResourceAsStream(resourceName);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        Pattern pattern = Pattern.compile("(?<=<path d=\")[\\s\\S]+(?=\"/>)");

        return scanner.findWithinHorizon(pattern, 0);
    }
}
