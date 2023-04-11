/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/8 上午10:05
 */

package cn.devspace.nucleus.App.Login.mapping;

import cn.devspace.nucleus.App.Login.Entity.tokenEntity;
import cn.devspace.nucleus.App.Login.Login;
import cn.devspace.nucleus.App.Login.units.tokenUnit;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.devspace.nucleus.Units.Unit;
import org.hibernate.Session;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

import static cn.devspace.nucleus.App.Login.units.tokenUnit.*;

public class token extends RouteManager {

    /**
     * 获取token
     * @param args 请求参数
     * @return 返回token实体
     */
    @Router("getToken")
    public String getToken(Map<String,String> args){
        Map<String, Object> result = new HashMap<>(10);
        // TODO: 这里的type应当开放式设计，可写入非指定的内容，内容由所属的Plugin/App提供校验
        String[] params = {"ltoken","type","time","app","appToken"};
        if (!checkParams(args,params)){
            return ResponseString(101,-1,"参数不合法");
        }else {
            if (!VerifyAppToken(args.get("app"),args.get("appToken"),args.get("time"))){
                return ResponseString(403,0,"token验证失败");
            }
            if (!VerifyLoginToken(args.get("ltoken"))){
                return ResponseString(403,0,"登陆失效");
            }
            DataBase database = Login.getDatabase();
            Session session = database.getSession();
            tokenEntity tokenEntity = new tokenEntity();
            String token = newToken(args.get("type"),args.get("appToken"));
            tokenEntity.setToken(token);
            tokenEntity.setType(args.get("type"));
            tokenEntity.setAllowTime(1000*60*60*24*3);
            tokenEntity.setUid(getUIDbyToken(args.get("ltoken")));
            session.save(tokenEntity);
            session.getTransaction().commit();

            result.put("token",token);
            result.put("code","200");
            result.put("status","1");
            return Map2Json(result);
        }

    }

    /**
     * 生成AppToken接口
     * @param args 需求参数: App名称
     * @return 返回结果
     */
    @Router("getAppToken")
    public Object getAppToken(Map<String,String> args){
        String[] params = {"App"};
        if (!checkParams(args, params)){
            return ResponseString(-1,-1,"非法参数");
        }else {
            if (!Unit.haveApp(args.get("App"))){
                return ResponseString(-2,-1,"不存在这个App");
            }else {
                Map<String ,String > res = new HashMap<>(5);
                Long time = System.currentTimeMillis();
                res.put("app",args.get("App"));
                res.put("time",String.valueOf(time));
                res.put("appToken",newAppToken(Server.PluginList.get(args.get("App")).getKey(), String.valueOf(time)));
                return res;
            }
        }
    }

    @Router("auth/token/test")
    public Object test(Map<String,String> args){
        login l = new login();
        try{
            String appName = "Centrosome";
            String time = String.valueOf(System.currentTimeMillis());
            String key = Server.PluginList.get(appName).getKey();
            String token = DigestUtils.md5DigestAsHex((time+key).getBytes());
            String type = "createNewPoll";
            //String token = tokenUnit.newToken();

            Map<String ,String> res = new HashMap<>(20);
            //res.put("ltoken",ltoken);
            res.put("type",type);
            res.put("time",time);
            res.put("app",appName);
            res.put("appToken",token);
            return res;
        }catch (Exception e)
        {
            Log.sendLog(e.getLocalizedMessage());
            Log.sendWarn(e.getCause().getMessage());
        }
        return null;

    }


}
