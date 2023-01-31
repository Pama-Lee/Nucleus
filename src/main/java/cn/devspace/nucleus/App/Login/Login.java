package cn.devspace.nucleus.App.Login;

import cn.devspace.nucleus.App.Login.Entity.tokenEntity;
import cn.devspace.nucleus.App.Login.command.baseCommands;
import cn.devspace.nucleus.App.Login.mapping.login;
import cn.devspace.nucleus.App.Login.mapping.reg;
import cn.devspace.nucleus.App.Login.mapping.token;
import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.Command.CommandBase;
import cn.devspace.nucleus.Manager.Command.CommandManager;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Manager.RouterBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.AppBase;
import org.hibernate.boot.model.relational.Database;

public class Login extends AppBase implements RouterBase {

    private LangBase lang;

    private static DataBase dataBase;

    /**
     * App 加载时
     */
    public void onLoad() {
        this.lang = loadLanguage();
        // 初始化数据库
        dataBase = new DataBase(this.getClass(), new tokenEntity());
        // 注册路由
        initRoute(token.class);
        initRoute(login.class);
        initRoute(reg.class);
        CommandManager.registerCommand(new baseCommands());
        sendLog(this.lang.Translate("Loading"));
    }

    /**
     * App 完成加载时
     */
    public void onEnable() {
        sendLog(this.lang.Translate("Enable"));
    }

    public static DataBase getDatabase(){
        return dataBase;
    }


}
