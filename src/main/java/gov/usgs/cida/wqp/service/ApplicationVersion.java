package gov.usgs.cida.wqp.service;

import java.util.Properties;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public final class ApplicationVersion implements ServletContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationVersion.class);
	
	private static ServletContext servletContext;
	
	public ApplicationVersion() {
		LOG.trace(getClass().getName());
	}
	
	public static String getVersion() {
		String currentVersion = "";
		try {
			String name = "/META-INF/MANIFEST.MF";
			Properties props = new Properties();
			props.load(servletContext.getResourceAsStream(name));
			String projectVersion = (String) props.get("Project-Version");
			currentVersion = projectVersion + (projectVersion.endsWith("-SNAPSHOT") ? "-" + (String) props.get("BuildTime") : "");
		} catch (Exception e) {
			LOG.warn("unable to get application version", e);
		}
		return currentVersion;
	}
	
	public void setServletContext(final ServletContext inServletContext) {
		servletContext = inServletContext;
	}
}