/**
 * _      ____        _  __     __ ______               _
 * | | /| / / /  __ __/ |/ /__  / //_  __/__ ___ ___ _  (_)__  ___ _
 * | |/ |/ / _ \/ // /    / _ \/ __// / / -_) _ `/  ' \/ / _ \/ _ `/
 * |__/|__/_//_/\_, /_/|_/\___/\__//_/  \__/\_,_/_/_/_/_/_//_/\_, /
 * /___/                                         /___/
 * Author: Pama Lee
 */
package cn.devspace.nucleus;


import cn.devspace.nucleus.App.Login.User;
import cn.devspace.nucleus.Manager.BeanManager;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.Server.Thread.WebServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class NucleusApplication {

    static BeanManager beanManager;

    @Resource
    public void mine(BeanManager beanManage){
        beanManager = beanManage;
    }
    //入口类
    public static void main(String[] args) {

        Server server = new Server();
        try {
            server.Start();
        } catch (Exception e) {
            Log.sendWarn(e.toString());
        }
        //init
        WebServer webServer = new WebServer(args);
        webServer.run();
        server.EnabledApp();
        server.EnabledPlugin();

    }

}
