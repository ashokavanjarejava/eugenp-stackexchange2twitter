package org.tweet.spring;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tweet.spring.util.SpringProfileUtil;

@Configuration
@PropertySource({ "classpath:persistence-${persistenceTarget:setup}.properties" })
@Profile(SpringProfileUtil.PERSISTENCE)
public class SetupPersistenceTestConfig {

    @Autowired
    private Environment env;

    public SetupPersistenceTestConfig() {
        super();
    }

    //

    @Bean
    public DataSource restDataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(restDataSource());
    }

}
