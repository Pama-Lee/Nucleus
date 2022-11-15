/**
 * _      ____        _  __     __ ______               _
 * | | /| / / /  __ __/ |/ /__  / //_  __/__ ___ ___ _  (_)__  ___ _
 * | |/ |/ / _ \/ // /    / _ \/ __// / / -_) _ `/  ' \/ / _ \/ _ `/
 * |__/|__/_//_/\_, /_/|_/\___/\__//_/  \__/\_,_/_/_/_/_/_//_/\_, /
 * /___/                                         /___/
 * Author: Pama Lee
 */
package cn.devspace.nucleus;


import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.Server.Thread.WebServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NucleusApplication {
    //入口类
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.Start();
        } catch (Exception e) {
            Log.sendWarn(e.toString());
        }
        //init
        WebServer webServer = new WebServer(args);
        webServer.run();
        Server.EnabledApp();
        Server.EnabledPlugin();
        Log.sendLog("测试");

    }

}
