package cn.devspace.nucleus.Manager.DataBase;

import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Manager.SettingManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Units.Unit;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@AutoConfigureAfter(SpringUtil.class)
public class DataSourceConfig {

   @Bean
   public DataSource getDataSource(){
      SettingManager settingManager = new SettingManager();
      Map<String,String> map = settingManager.getMapSetting("DataBase");
      String[] params = {"database","port","username","password"};
      if (!Unit.checkParams(map,params)){
         Log.sendWarn("未配置完成数据源");
         return null;
      }
      DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
      dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
      String port = map.get("port");
      dataSourceBuilder.url("jdbc:mysql://localhost:"+ port +"/"+map.get("database")+"?characterEncoding=utf-8");
      dataSourceBuilder.username(map.get("username"));
      dataSourceBuilder.password(map.get("password"));
      DataSource dataSource = dataSourceBuilder.build();
      Log.sendLog("数据库配置中");
      return dataSource;
   }
}