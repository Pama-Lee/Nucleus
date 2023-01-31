package cn.devspace.nucleus.App.Register;

import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Plugin.AppBase;

import java.util.Map;

public class Register extends AppBase {

    @Override
    public void onLoad() {
        sendLog("小飞棍来喽");
    }

    @Override
    public String onCall(String route, String method, Map<String, String> Request) {
        sendLog("有人来了:" + Request.toString());
        return Request.toString();
    }

    @Router("reg")
    public String reg() {
        return "欢迎注册!";
    }
}
