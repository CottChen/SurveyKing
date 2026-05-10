package cn.surveyking.server.analytics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("surveyking.analytics.datasource")
public class AnalyticsDataSourceProperties {

	private String url;

	private String username;

	private String password;

	private String driverClassName = "org.postgresql.Driver";

}
