package cn.devspace.nucleus.Manager.DataBase;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;


import javax.sql.DataSource;

import static com.baomidou.mybatisplus.extension.toolkit.SqlHelper.sqlSessionFactory;

public class MyBatisConfig {

    public SqlSessionFactory addMapper(Class<?> clazz) {
        // Get DataSource object.
        DataSource dataSource = MyBatisConfig.getDataSource();

        // Creates a transaction factory.
        TransactionFactory trxFactory = new JdbcTransactionFactory();

        // Creates an environment object with the specified name, transaction
        // factory and a data source.
        Environment env = new Environment("dev", trxFactory, dataSource);

        // Creates a Configuration object base on the Environment object.
        // We can also add type aliases and mappers.
        Configuration config = new Configuration(env);
        TypeAliasRegistry aliases = config.getTypeAliasRegistry();
        aliases.registerAlias("record", Record.class);

        config.addMapper(clazz);

        // Build the SqlSessionFactory based on the created Configuration object.
        // Open a session and query a record using the RecordMapper.
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);
        return factory;
    }

    /**
     * Returns a DataSource object.
     *
     * @return a DataSource.
     */
    public static DataSource getDataSource() {
         DataSource dataSource = new org.apache.ibatis.datasource.pooled.PooledDataSource(
                "", "", "", "");
        return dataSource;
    }
}
