package cn.devspace.nucleus.Manager.DataBase;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import javax.sql.DataSource;

@Configuration
public class MybatisConfig {
    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("dataSource") DataSource dataSource) throws Exception {

        // 可添加自定义拦截器，没有自定义拦截器的小伙伴可忽略此部分
        // MybatisConfiguration
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        mybatisConfiguration.setMapUnderscoreToCamelCase(false);
        mybatisConfiguration.addInterceptor(interceptor);

        // 开启缓存，可自行选择
//        mybatisConfiguration.setCacheEnabled(true);

        // 使用MybatisSqlSessionFactoryBean
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();

        // 设置数据源
        sqlSessionFactoryBean.setDataSource(dataSource);

        // 添加MybatisConfiguration
        sqlSessionFactoryBean.setConfiguration(mybatisConfiguration);

        // 添加全局配置，设置MetaObjectHandler
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        //mybatis-plus全局配置设置元数据对象处理器为自己实现的那个
        //这个TimeMetaObjectHandler需要自己实现，没有就注释掉
       // globalConfig.setMetaObjectHandler(new TimeMetaObjectHandler());

        sqlSessionFactoryBean.setGlobalConfig(globalConfig);

        // 返回MybatisSqlSessionFactoryBean从而替代原生的sqlSessionFactory
        return sqlSessionFactoryBean.getObject();
    }
}
