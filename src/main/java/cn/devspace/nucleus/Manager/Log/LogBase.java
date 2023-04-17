package cn.devspace.nucleus.Manager.Log;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogBase {

    // 单例模式
    private static LogBase instance = null;

    private static String logPath = Server.RunPath + "/logs";

    private static File file = null;

    private FileWriter fileWriter = null;

    private PrintWriter printWriter = null;

    private boolean isInit = false;

    public static LogBase getInstance(){
        if (instance == null){
            instance = new LogBase();
        }
        return instance;
    }

    public LogBase(){
        // 初始化日志
        init();
    }

    public void sendLog(String message){
        if (!isInit){
            init();
        }
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        printWriter.println(timestamp + message);
        printWriter.flush();
    }

    public void sendLog(String message, Throwable e){
        if (!isInit){
            init();
        }
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        printWriter.println(timestamp + message);
        e.printStackTrace(printWriter);
        printWriter.flush();

        // 保存
        try {
            fileWriter.flush();
            printWriter.flush();
        }catch (Exception e1){
            Log.sendError("Can't save log file!",111);
        }

    }

    private void init(){
        // 初始化日志
        // 找到日志目录
        if (!new File(Server.RunPath + "/logs").exists()){
            new File(Server.RunPath + "/logs").mkdir();
        }

        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(new Date());
        // 创建日志文件
        File logFile = new File(Server.RunPath + "\\logs\\" + timestamp + ".txt");
        System.out.println(logFile.getAbsolutePath());

        try {
            if(logFile.createNewFile()){
                file = logFile;
            } else {
                // 输出错误
                Log.sendError("Can't create log file!",111);

            }
        }catch (Exception e){
            e.printStackTrace();
            Log.sendError("Can't create log file!",111);
        }

        // 初始化文件写入器
        try {
            fileWriter = new FileWriter(file);
            printWriter = new PrintWriter(fileWriter);
        }catch (Exception e){
            Log.sendError("Can't create log file writer!",111);
        }

        isInit = true;
    }


}
