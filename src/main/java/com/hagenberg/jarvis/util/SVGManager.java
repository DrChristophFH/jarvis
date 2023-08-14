package com.hagenberg.jarvis.util;

import javafx.scene.shape.SVGPath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SVGManager {
    private static final String DEFAULT_PATH = "m 4 1 c -1.644531 0 -3 1.355469 -3 3 v 1 h 1 v -1 c 0 -1.109375 0.890625 -2 2 -2 h 1 v -1 z m 2 0 v 1 h 4 v -1 z m 5 0 v 1 h 1 c 1.109375 0 2 0.890625 2 2 v 1 h 1 v -1 c 0 -1.644531 -1.355469 -3 -3 -3 z m -5 4 c -0.550781 0 -1 0.449219 -1 1 s 0.449219 1 1 1 s 1 -0.449219 1 -1 s -0.449219 -1 -1 -1 z m -5 1 v 4 h 1 v -4 z m 13 0 v 4 h 1 v -4 z m -4.5 2 l -2 2 l -1.5 -1 l -2 2 v 0.5 c 0 0.5 0.5 0.5 0.5 0.5 h 7 s 0.472656 -0.035156 0.5 -0.5 v -1 z m -8.5 3 v 1 c 0 1.644531 1.355469 3 3 3 h 1 v -1 h -1 c -1.109375 0 -2 -0.890625 -2 -2 v -1 z m 13 0 v 1 c 0 1.109375 -0.890625 2 -2 2 h -1 v 1 h 1 c 1.644531 0 3 -1.355469 3 -3 v -1 z m -8 3 v 1 h 4 v -1 z m 0 0";
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

        if (inputStream == null) {
            return DEFAULT_PATH;
        }

        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        Pattern pattern = Pattern.compile("(?<=<path d=\")[\\s\\S]+(?=[ \n]*\"[ \n]*/>)");

        return scanner.findWithinHorizon(pattern, 0);
    }
}
