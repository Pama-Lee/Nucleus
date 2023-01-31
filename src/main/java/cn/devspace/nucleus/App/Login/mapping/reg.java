/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/11 下午10:12
 */

package cn.devspace.nucleus.App.Login.mapping;

import cn.devspace.nucleus.App.Login.Entity.User;
import cn.devspace.nucleus.App.Login.Login;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import org.hibernate.Session;
import org.springframework.util.DigestUtils;

import java.util.Map;

public class reg extends RouteManager {

    @Router("auth/register")
    public Object register(Map<String,String> args){
        //检查参数
        String[] params = {"email","pwd","t"};
        if (!checkParams(args,params)){
            return ResponseString(-1,0,"非法参数");
        }
        DataBase dataBase = Login.getDatabase();
        Session session = dataBase.newSession("reg",this.getClass(), new User());
        User usr = new User();
        //盐
        String salt = "nucleus-";
        String pwd = args.get("pwd");
        String email = args.get("email");
        String t = args.get("t");
        usr.setPwd(DigestUtils.md5DigestAsHex((salt+pwd).getBytes()));
        usr.setEmail(email);
        session.save(usr);
        try {
            session.getTransaction().commit();
            return ResponseString(200,1,"注册成功");
        } catch (Exception e) {
            return ResponseString(100,-1,"提交失败");
        }

    }

}
