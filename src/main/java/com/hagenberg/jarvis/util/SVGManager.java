package com.hagenberg.jarvis.util;

import javafx.scene.shape.SVGPath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SVGManager {
    private static SVGManager instance;
    private final Map<String, SVGPath> cache = new HashMap<>();

    private SVGManager() {
    }

    public static SVGManager getInstance() {
        if (instance == null) {
            instance = new SVGManager();
        }
        return instance;
    }

    public SVGPath getSVG(String resourceName) {
        return cache.computeIfAbsent(resourceName, this::loadSVG);
    }

    private SVGPath loadSVG(String resourceName) {
        InputStream inputStream = getClass().getResourceAsStream(resourceName);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        Pattern pattern = Pattern.compile("(?<=<path d=\")[\\s\\S]+(?=\"/>)");

        String content = scanner.findWithinHorizon(pattern, 0);
        System.out.println(content);

        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);

        return svgPath;
    }
}
