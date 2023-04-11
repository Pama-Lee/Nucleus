/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/11 下午8:58
 */

package cn.devspace.nucleus.App.Login.mapping;

import cn.devspace.nucleus.App.Login.Entity.User;
import cn.devspace.nucleus.App.Login.Entity.tokenEntity;
import cn.devspace.nucleus.App.Login.Login;
import cn.devspace.nucleus.App.Login.TokenType;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;


public class login extends RouteManager {

    public static login ins;

    @Router("auth/login")
    public Object loginMethod(Map<String,String> args){
        ins = this;
        try{
            String[] params = {"email","pwd","t"};
            if (!checkParams(args,params)){
                return ResponseString(101,-1,"非法参数");
            }else {

                Map<String, Object> result = new HashMap<>(10);

                DataBase dataBase = Login.getDatabase();
                Session session = dataBase.newSession("user", this.getClass(),new User());

                String email = args.get("email");
                String pwd = args.get("pwd");
                String time = args.get("t");
                Long currentTime = System.currentTimeMillis();
                if (currentTime - Long.parseLong(time) > 10000){
                    return ResponseString(-1,0,"非法请求"+currentTime+"-"+time);
                }
                String salt = "nucleus-";
                String password = DigestUtils.md5DigestAsHex((salt+pwd).getBytes());

                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email",email))
                        .add(Restrictions.eq("pwd",password));
                User usere =  (User) criteria.uniqueResult();
                if (usere == null){
                    return ResponseString(-1,-1,"找不到该用户");
                }else {
                    Long uid = usere.getUid();
                    String token = createLoginToken(uid,time,email);
                    if (token == null){
                        return ResponseString(-1,0,"登陆错误");
                    }
                    result.put("token",token);
                    result.put("status","1");
                    result.put("code","200");
                    return Map2Json(result);
                }
            }
        }catch (Exception e){
            Log.sendWarn(e.toString());
        }
      return null;
    }


   public String createLoginToken(Long UID,String time,String email){
        Session session = Login.getDatabase().newSession("token",this.getClass(),new tokenEntity());
        String salt = "cLt-nucleus";
        String token = DigestUtils.md5DigestAsHex((time+email+salt).getBytes());
        tokenEntity tokenEntity = new tokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setType(TokenType.Main_Login);
        tokenEntity.setUid(UID);
        //3天过期时间
        tokenEntity.setAllowTime(1000*60*60*24*3);
        try {
            session.save(tokenEntity);
            session.getTransaction().commit();
            return token;
        }catch (Exception e){
            Log.sendWarn(e.toString());
            return null;
        }
    }

    public static login getIns(){
        return ins;
    }


}
