package cn.surveyking.server.analytics;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(AnalyticsDataSourceProperties.class)
public class AnalyticsDataSourceConfiguration {

	@Bean("analyticsJdbcTemplate")
	public NamedParameterJdbcTemplate analyticsJdbcTemplate(DataSource dataSource,
			AnalyticsDataSourceProperties properties) {
		if (StringUtils.isBlank(properties.getUrl())) {
			return new NamedParameterJdbcTemplate(dataSource);
		}
		DataSource analyticsDataSource = DataSourceBuilder.create().driverClassName(properties.getDriverClassName())
				.url(properties.getUrl()).username(properties.getUsername()).password(properties.getPassword()).build();
		return new NamedParameterJdbcTemplate(analyticsDataSource);
	}

}
