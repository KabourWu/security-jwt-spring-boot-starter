package online.kabour.springbootbase.securityjwt.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kabour
 * @date 2019/6/19
 */
@ConfigurationProperties(prefix = "security.ajax")
public class AjaxSettings {

	/**
	 * Is enable Ajax Login Filter
	 */
	private Boolean enabled = false;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
