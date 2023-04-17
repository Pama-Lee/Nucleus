package cn.devspace.nucleus.Message;

import cn.devspace.nucleus.Manager.Log.LogBase;
import cn.devspace.nucleus.Server.Server;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Log extends MessageBase {

    private static String plugin = null;

    public static void AppStart(String AppName) {
        AnsiConsole.systemInstall(); 
        System.out.print(BaseFormat(METHOD_APP, AppName));
        AnsiConsole.systemUninstall();
        LogBase.getInstance().sendLog("App " + AppName + " started.");
    }

    public static void sendLog(String Message) {
        AnsiConsole.systemInstall(); 
        System.out.print(BaseFormat(METHOD_LOG, Message));
        AnsiConsole.systemUninstall();
        LogBase.getInstance().sendLog(Message);
    }

    public static void sendAppMessage(String App, String Message) {
        AnsiConsole.systemInstall(); 
        plugin = App;
        System.out.print(BaseFormat(METHOD_APPMESSAGE, Message));
        AnsiConsole.systemUninstall();
        LogBase.getInstance().sendLog("[" + App + "] " + Message);
    }

    public static void sendWarn(String Message) {
        AnsiConsole.systemInstall(); 
        System.out.print(BaseFormat(METHOD_WARN, Message));
        AnsiConsole.systemUninstall();
        LogBase.getInstance().sendLog(Message);
    }

    public static void sendError(String Message, int Code) {
        System.out.print(BaseFormat(METHOD_ERROR, Message));
        Server.Shutdown(Code);
        LogBase.getInstance().sendLog(Message);
    }

    public static String BaseFormat(String METHOD, String Message) {
        // 检测操作系统
        // Check the operating system
        String OS = System.getProperty("os.name").toLowerCase();
            if (OS.contains("windows")) {
            // 在 Windows 上设置控制台编码为 UTF-8
            // Set the console encoding to UTF-8 on Windows
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        }

        String Prefix = switch (METHOD) {
            case "LOG" -> Format(PREFIX_LOG, BLUE_COLOR) + Format(PREFIX, WHITE_COLOR);
            case "APP" -> Format(PREFIX_APP, GREEN_COLOR) + Format(PREFIX, WHITE_COLOR);
            case "WARN" -> Format(PREFIX_WARN, RED_COLOR) + Format(PREFIX, WHITE_COLOR);
            case "ERROR" -> Format(PREFIX_ERROR, RED_COLOR) + Format(PREFIX, RED_COLOR);

            default -> null;
        };
        if (METHOD.equals("APPMESSAGE")) {
            Prefix = Format("[<" + plugin + ">]", YELLOW_COLOR);
        }
        return Format(getTime(), PINK_COLOR) + "->" + Prefix + "\t" + Format(Message, WHITE_COLOR) + "\n";

    }
}
