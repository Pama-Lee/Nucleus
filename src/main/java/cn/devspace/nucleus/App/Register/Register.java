package cn.devspace.nucleus.App.Register;

import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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

    @Router(URI = "reg", isUpload = true)
    public Object reg(Map<String, Object> args, MultipartFile file) {
        Log.sendLog(args.toString());
        // 输出文件的所有信息
        Map<String, String> info = new HashMap<>();
        info.put("name", file.getName());
        info.put("size", file.getSize() + "");
        info.put("type", file.getContentType());
        info.put("filename", file.getOriginalFilename());
        return info;
    }
}
