package cn.devspace.nucleus.Server.Thread;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.NucleusApplication;
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
        SpringApplication.run(NucleusApplication.class, this.args);

    }
}
