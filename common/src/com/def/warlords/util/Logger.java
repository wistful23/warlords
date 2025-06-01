package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public final class Logger {

    private static boolean verbose;

    public static void invertVerbose() {
        verbose = !verbose;
        dev("Verbose logging: " + (verbose ? "ON" : "OFF"));
    }

    public static void info(String msg) {
        if (verbose) {
            System.out.println("[INF][" + getLocation() + "] " + msg);
        }
    }

    public static void warn(String msg) {
        System.out.println("[WRN][" + getLocation() + "] " + msg);
    }

    public static void error(String msg) {
        System.out.println("[ERR][" + getLocation() + "] " + msg);
    }

    public static void dev(String msg) {
        System.out.println("[DEV][" + getLocation() + "] " + msg);
    }

    private static String getLocation() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        final String className = e.getClassName();
        return className.substring(className.lastIndexOf('.') + 1) + '.' + e.getMethodName() + ':' + e.getLineNumber();
    }

    private Logger() {
    }
}
