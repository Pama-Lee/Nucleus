/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2023/1/31 下午10:32
 */

package cn.devspace.nucleus.App.Permission;

import cn.devspace.nucleus.App.Permission.unit.permissionManager;
import cn.devspace.nucleus.Lang.LangBase;
import cn.devspace.nucleus.Plugin.AppBase;
import cn.devspace.nucleus.Units.Unit;

public class Main extends AppBase {

    public LangBase langBase;

    @Override
    public void onLoad() {
        this.langBase = loadLanguage();
        sendLog(this.langBase.TranslateOne("Loading"));
        String[] group = {"president","developer","manager"};
    }
}
