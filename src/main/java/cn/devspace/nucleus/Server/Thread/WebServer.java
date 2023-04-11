package cn.devspace.nucleus.Server.Thread;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.NucleusApplication;
import cn.devspace.nucleus.Server.Server;
import org.springframework.boot.SpringApplication;

public class WebServer implements Runnable {

    private String[] args;

    public WebServer(String[] args) {
        this.args = args;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        Log.sendLog(Server.getInstance().TranslateOne("Server.Open"));
        // 设置服务器端口
        System.setProperty("server.port", Server.getPort());
        SpringApplication.run(NucleusApplication.class, this.args);
    }
}
