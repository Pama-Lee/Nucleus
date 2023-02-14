/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/25 下午11:08
 */

package cn.devspace.nucleus.App.Login.command;

import cn.devspace.nucleus.App.Login.mapping.token;
import cn.devspace.nucleus.Manager.Annotation.Commands;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.ManagerBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class baseCommands implements CommandBase {
    @Commands(Command = "login",help = "/login newTestUser-创建一个新的随机用户数据")
    public void loginBase(String[] args){
        if (args!=null && args.length > 0 && args[0].equals("newTestUser")){
            Log.sendLog(ManagerBase.Map2Json((Map<String, String>) new token().test(new HashMap<>())));
        }
    }

    @Commands(Command = "getKey")
    public void getKey(String[] args){
        Log.sendLog("Server:"+Server.PluginList.get("Centrosome").getKey());
    }

}
