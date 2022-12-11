package cn.devspace.nucleus.App.Console;

import cn.devspace.nucleus.App.Console.Thread.ConsoleThread;
import cn.devspace.nucleus.Manager.Annotation.Commands;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.Command.CommandManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Server.Server;

import java.util.Arrays;

import static cn.devspace.nucleus.Server.Server.getUsedMemory;


public class Console extends AppBase implements CommandBase {

    @Commands(Command = "help")
    public String help(String[] args) {
        sendLog("这是Help命令!");
        sendLog("参数:" + Arrays.toString(args));
        return "123";
    }

    @Commands(Command = "test")
    public String test(String[] args){
        Log.sendLog(Server.RouterList.toString());
        return null;
    }

    @Override
    public void onLoad() {
        sendLog("控制台正在启动");
        CommandManager.registerCommand(this);
    }

    @Commands(Command = "load")
    public void load(String args[]){
        Log.sendLog(String.valueOf(getUsedMemory()));
    }

    @Override
    public void onEnabled() {
        ConsoleThread CT = new ConsoleThread();
        CT.start();
        sendLog("你可以输入命令,命令以\"/\"开始,您可以输入/help查看帮助");
    }
}
