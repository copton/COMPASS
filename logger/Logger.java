package logger;

public class Logger {
    public static void fatal(String module, String function, String message)
    {
        System.out.println("Fatal Error: " + module + "." + function + ": " + message);
    }

    public static void error(String module, String function, String message)
    {
        System.out.println("Error: " + module + "." + function + ": " + message);
    }

    public static void debug(String module, String function, String message)
    {
        System.out.println("Debug: " + module + "." + function + ": " + message);
    }
}
