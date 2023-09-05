package com.hagenberg.jarvis.debuggee;

public class JDIExampleDebuggee {
//    public static void main(String[] args) {
//        String jpda = "Java Platform Debugger Architecture";
//        System.out.println("Hi Everyone, Welcome to " + jpda); // add a break point here
//
//        String jdi = "Java Debug Interface"; // add a break point here and also stepping in here
//        String text = "Today, we'll dive into " + jdi;
//        System.out.println(text);
//
//        HashMap<Integer, String> testMap = new HashMap<>(11, 0.30f);
//        System.out.println(testMap);
//        testMap.put(1, "one");
//        testMap.put(11, "twelve");
//        testMap.put(21, "twenty-one");
//        testMap.put(31, "thirty-one");
//        testMap.put(41, "forty-one");
//        testMap.put(2, "two");
//        testMap.put(3, "three");
//        for (KeyValuePair<Integer, String> item : testMap) {
//            System.out.println(item.getValue());
//        }
//    }

    public static void main(String[] args) {
        Robot robot = new Robot();
        robot.whereAreYou();
        robot.move(1, 2, 3);
        robot.howAreYou();
    }
}
