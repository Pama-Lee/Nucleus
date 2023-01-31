/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2022/12/27 下午11:26
 */

package cn.devspace.nucleus.App.Login.units;

import cn.devspace.nucleus.App.Login.Entity.tokenEntity;
import cn.devspace.nucleus.App.Login.Login;
import cn.devspace.nucleus.App.Login.TokenType;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Plugin.DataEntity;
import cn.devspace.nucleus.Server.Server;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.DigestUtils;

import java.util.List;

public class tokenUnit extends AppBase {

    private static DataBase dataBase;


    /**
     * 检查登陆主token状态
     * @param ltoken 登陆主token
     * @return 返回token是否有效
     */
    public static boolean checkLoginToken(String ltoken){
        Session login_session = getSession("token", tokenUnit.class, new tokenEntity());
        Criteria criteria = login_session.createCriteria(tokenEntity.class);
        criteria.add(Restrictions.eq("token",ltoken))
                .add(Restrictions.eq("type", TokenType.Main_Login));
        tokenEntity tokenEntity = (tokenEntity) criteria.uniqueResult();
        return tokenEntity != null && tokenEntity.getAllowTime() >= System.currentTimeMillis();// 同时比较过期时间
    }

    /**
     * 通过登陆token获取用户ID
     * @param ltoken
     * @return
     */
    public static Long getUIDbyLoginToken(String ltoken){
        if (checkLoginToken(ltoken)){
            Session login_session = getSession("token", tokenUnit.class,new tokenEntity());
            Criteria criteria = login_session.createCriteria(tokenEntity.class);
            criteria.add(Restrictions.eq("token",ltoken))
                    .add(Restrictions.eq("type",TokenType.Main_Login));
            tokenEntity tokenEntity = (tokenEntity) criteria.uniqueResult();
            if (tokenEntity != null){
                //Log.sendLog(tokenEntity.getUid().toString());
                return tokenEntity.getUid();
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    public static Session getSession(String session, Class<?> clazz, DataEntity dataEntity){
        dataBase = Login.getDatabase();
        if (dataBase.getSession(session)!=null){
            return dataBase.getSession(session);
        }else {
            return dataBase.newSession(session,clazz,dataEntity);
        }
    }

    public static boolean VerifyLoginToken(String LoginToken){
        Session session = Login.getDatabase().getSession();
        Criteria criteria = session.createCriteria(tokenEntity.class);
        criteria.add(Restrictions.eq("token",LoginToken))
                .add(Restrictions.eq("type", TokenType.Main_Login));
        tokenEntity token = (tokenEntity) criteria.uniqueResult();
        return token != null && token.getAllowTime() > System.currentTimeMillis();
    }

    /**
     * 返回加密后的AppToken
     * @param AppKey App内部key
     * @param time App内部生成的time
     * @return
     */
    public static String newAppToken( String AppKey,String time){
        return DigestUtils.md5DigestAsHex((time+AppKey).getBytes());
    }

    /**
     * 验证App的Token
     * @param AppName 传入App名
     * @param AppToken 传入Token
     * @param time 传入时间
     * @return 返回验证结果
     */
    public static boolean VerifyAppToken(String AppName, String AppToken, String time){
        if (Server.AppList.containsKey(AppName)){
            String AppKey = Server.AppList.get(AppName).getKey();
            String CorrectKey = DigestUtils.md5DigestAsHex((time+AppKey).getBytes());
            return AppToken.equals(CorrectKey);
        } else if (Server.PluginList.containsKey(AppName)) {
            String AppKey = Server.PluginList.get(AppName).getKey();
            // App加密方式 时间戳+AppKey
            String CorrectKey = DigestUtils.md5DigestAsHex((time+AppKey).getBytes());
            return AppToken.equals(CorrectKey);
        }else {
            return false;
        }
    }

    /**
     * 验证权限Token是否正确
     * @param PerToken 权限token
     * @param type 传入权限token的类型
     * @param AppName App/Plugin 名
     * @param AppToken App的内部token
     * @param time App内部时间戳
     * @return 返回权限Token是否正确
     */
    public static boolean VerifyPermissionToken(String PerToken,String type, String AppName, String AppToken, String time){
        //TODO:
        if (!VerifyAppToken(AppName,AppToken,time)){
            return false;
        }
        DataBase database = Login.getDatabase();
        Session PermissionSession = database.newSession("permissionToken", tokenUnit.class,new tokenEntity());
        Criteria criteria = PermissionSession.createCriteria(tokenEntity.class);
        criteria.add(Restrictions.eq("token",PerToken))
                .add(Restrictions.eq("type",type));
        List<tokenEntity> list = criteria.list();
        if (list == null){
            return false;
        }else {
            Long allowTime = list.get(0).getAllowTime();
            Long currentTime = System.currentTimeMillis();
            return allowTime > currentTime;
        }
    }

    /**
     * 通过有效的token获取UID
     * @param Token 传入token(不限类型)
     * @return 返回UID
     */
    public static Long getUIDbyToken(String Token){
        DataBase dataBase = Login.getDatabase();
        Session session = dataBase.newSession("token", tokenUnit.class,new tokenEntity());
        Criteria criteria = session.createCriteria(tokenEntity.class);
        criteria.add(Restrictions.eq("token",Token));
        List<tokenEntity> list = criteria.list();
        if (list.get(0).getAllowTime() < System.currentTimeMillis()){
            return 0L;
        }else {
            return list.get(0).getUid();
        }

    }

    /**
     * 创建一个新Token
     * @param type token的类型
     * @param appToken App的内部token
     * @return 返回String类型的token
     */
    public static String newToken(String type, String appToken){
        return DigestUtils.md5DigestAsHex((type+appToken+System.currentTimeMillis()).getBytes());
    }


}
