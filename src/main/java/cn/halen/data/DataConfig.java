package cn.halen.data;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ImportResource({"classpath:/config.xml", "classpath:/security.xml"})
@ComponentScan(basePackageClasses = { DataConfig.class })
public class DataConfig {

	@Value("${h2.db.host}")
	private String h2DbHost;
	@Value("${h2.db.port}")
	private int h2DbPort;
	@Value("${h2.db.name}")
	private String h2DbName;
	@Value("${h2.db.username}")
	private String h2DbUsername;
	@Value("${h2.db.password}")
	private String h2DbPassword;
	
	@Value("${mysql.db.host}")
	private String mysqlDbHost;
	@Value("${mysql.db.port}")
	private int mysqlDbPort;
	@Value("${mysql.db.name}")
	private String mysqlDbName;
	@Value("${mysql.db.username}")
	private String mysqlDbUsername;
	@Value("${mysql.db.password}")
	private String mysqlDbPassword;

	@Bean
	public JdbcTemplate jdbcTemplate() {

		return new JdbcTemplate(mysqlDataSource());
	}
	
	@Bean()
	public DataSource mysqlDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		String url = String.format("jdbc:mysql://%s:%s/%s", mysqlDbHost, mysqlDbPort,
				mysqlDbName);
		dataSource.setUrl(url);
		dataSource.setUsername(mysqlDbUsername);
		dataSource.setPassword(mysqlDbPassword);
		dataSource.addConnectionProperty("useUnicode", "true");
		dataSource.addConnectionProperty("characterEncoding", "UTF-8");
		dataSource.setMaxActive(4);
		dataSource.setMaxIdle(2);
		dataSource.setInitialSize(2);
		dataSource.setRemoveAbandoned(true);
		dataSource.setRemoveAbandonedTimeout(180);
		dataSource.setTestOnBorrow(true);
		// dataSource.setValidationQuery("select sysdate from dual");
		// dataSource.setValidationQueryTimeout(1);
		//dataSource.setDefaultReadOnly(true);
		dataSource.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		return dataSource;
	}

//	public DataSource h2DataSource() {
//		BasicDataSource dataSource = new BasicDataSource();
//		String url = String.format("jdbc:h2:tcp://%s:%s/%s", h2DbHost, h2DbPort,
//				h2DbName);
//		dataSource.setUrl(url);
//		dataSource.setUsername(h2DbUsername);
//		dataSource.setPassword(h2DbPassword);
//		dataSource.setMaxActive(4);
//		dataSource.setMaxIdle(2);
//		dataSource.setInitialSize(2);
//		dataSource.setRemoveAbandoned(true);
//		dataSource.setRemoveAbandonedTimeout(180);
//		dataSource.setTestOnBorrow(true);
//		// dataSource.setValidationQuery("select sysdate from dual");
//		// dataSource.setValidationQueryTimeout(1);
//		dataSource.setDefaultReadOnly(true);
//		dataSource.setDriverClassName(Driver.class.getName());
//		return dataSource;
//	}

}
