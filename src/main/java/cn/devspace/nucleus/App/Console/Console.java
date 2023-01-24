package cn.devspace.nucleus.App.Console;

import cn.devspace.nucleus.App.Console.Thread.ConsoleThread;
import cn.devspace.nucleus.Manager.Annotation.Commands;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.Command.CommandManager;
import cn.devspace.nucleus.Manager.DataBase.test;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Server.Server;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import javax.annotation.Resource;

import static cn.devspace.nucleus.Server.Server.*;


public class Console extends AppBase implements CommandBase {

    @Resource
    public BaseMapper<test> testBaseMapper;

    @Commands(Command = "help",help = "/help [页数] 查看服务器所有指令")
    public String help(String[] args) {
        int page = 0;
        int helps = 20;// 每页Help的显示条数
        if (args != null){
            page = Integer.parseInt(args[0]);
        }else {
            page = 1;
        }
        Log.sendLog("欢迎使用"+Server.getName());
        int helpNum = CommandHelpMessage.size();
        int pages = helpNum%helps!=0?(helpNum/helps)+1:helpNum/helps;
        Log.sendLog("共有"+pages+"页,当前第"+page+"页");
        int temp = 1;
        int start = helps*page-(helps-1);
        int end = helps*page;
        Log.sendLog("===========================");
        for (String command:CommandHelpMessage.keySet()){
            if (temp < start|temp>end){
                temp++;
                continue;
            }
            Log.sendLog("/"+command+"->"+CommandHelpMessage.get(command));
            temp++;
        }
        Log.sendLog("===========================");
        Log.sendLog("输入[/help 页数]访问其他帮助信息,例如输入/help 2");
        //sendLog("参数:" + Arrays.toString(args));
        return "123";
    }

    @Commands(Command = "reload",help = "/reload [参数一],参数一可选:plugin,server,app. 重启服务器选定部分")
    public String reload(String[] args){
        if (!args[0].isEmpty() & "plugin".equals(args[0])){
            sendLog("重新加载插件中...");
            Server.getInstance().initPlugins(true);
        }else {
            sendLog("请检查参数");
        }
        return null;
    }

    @Commands(Command = "plugins",help = "查看服务器成功加载的插件")
    public String plugins(String[] args){
        int pluginNum = PluginList.size();
        StringBuilder plugins = new StringBuilder();
        for (String plugin:PluginList.keySet()){
            plugins.append(plugin+",");
        }
        sendLog("正在运行"+pluginNum+"个插件:"+plugins);
        return null;
    }

    @Commands(Command = "test", help = "测试命令")
    public String test(String[] args){
        //Log.sendLog(Server.RouterList.toString());
        testBaseMapper.selectList(null);
        return null;
    }

    @Override
    public void onLoad() {
        sendLog("控制台正在启动");
        CommandManager.registerCommand(this);
    }

    @Commands(Command = "load",help = "查看服务器内存占用")
    public void load(String args[]){
        Log.sendLog("目前占用内存:"+String.valueOf(getUsedMemory())+"MB");
    }

    @Commands(Command = "loaded",help = "查看服务器内存占用")
    public void loaded(String args[]){
        Log.sendLog("目前占用内存:"+String.valueOf(getUsedMemory())+"MB");
    }

    @Override
    public void onEnabled() {
        ConsoleThread CT = new ConsoleThread();
        CT.start();
        sendLog("你可以输入命令,命令以\"/\"开始,您可以输入/help查看帮助");
    }
}
