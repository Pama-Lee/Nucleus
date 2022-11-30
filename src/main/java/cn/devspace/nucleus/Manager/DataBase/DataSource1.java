package cn.devspace.nucleus.Manager.DataBase;

import lombok.Data;

@Data
public class DataSource1 {
    String isShutdown = "false";
   String fastPathPool = null;
   String pool = null;
   String catalog = null;
   int connectionTimeout = 30000;
   int validationTimeout = 5000;
   int idleTimeout = 600000;
   int leakDetectionThreshold = 0;
   int maxLifetime = 1800000;
   int maxPoolSize = -1;
   int minIdle = -1;
   String username = "root";
   String password = "123456";
   int initializationFailTimeout = 1;
   String connectionInitSql = null;
   String connectionTestQuery = null;
   String dataSourceClassName = null;
   String dataSourceJndiName = null;
   String driverClassName = "com.mysql.cj.jdbc.Driver";
   String exceptionOverrideClassName = null;
   String jdbcUrl = "jdbc:mysql://localhost:3306/test?characterEncoding=utf-8";
   String poolName = null;
   String schema = null;
   String transactionIsolationName = null;
   boolean isAutoCommit = true;
   boolean isReadOnly = false;
   boolean isIsolateInternalQueries = false;
   boolean isRegisterMbeans = false;
   boolean isAllowPoolSuspension = false;
   String dataSource = null;
   int dataSourceProperties = 0;
   String threadFactory = null;
   String scheduledExecutor = null;
   String metricsTrackerFactory = null;
   String metricRegistry = null;
   String healthCheckRegistry = null;
   int healthCheckProperties = 0;
   int keepaliveTime = 0;
   boolean sealed = false;
}
