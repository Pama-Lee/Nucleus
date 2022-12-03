package cn.devspace.nucleus.App.Console.Thread;

import cn.devspace.nucleus.Manager.Command.CommandManager;

import java.util.Scanner;

import static cn.devspace.nucleus.Message.Log.sendLog;

public class ConsoleThread extends Thread {


    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            // 读取整行
            String command = scanner.nextLine();
            CommandManager.ConsoleSupport(command);
        }
    }
}
