package org.zero.common.core.util.java;

import lombok.SneakyThrows;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/4
 */
public class MavenToGradleBuilder {
	protected File pomFile;
	protected File libsFile;

	@SneakyThrows
	public void pomToLibs() {
		MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
		Model model = mavenXpp3Reader.read(new FileReader(pomFile));
		Properties properties = model.getProperties();
		List<Dependency> dependencies = model.getDependencies();
		List<Dependency> managedDependencies = model.getDependencyManagement().getDependencies();
	}

	protected Properties processVariables(Properties properties) {
		Properties processedProperties = new Properties();
		Set<String> keys = properties.stringPropertyNames();
		for (String key : keys) {
			String value = properties.getProperty(key);
			if (value.startsWith("${")) {
				String val = value.replace("${", "").replace("}", "");
				String processedValue = processedProperties.getProperty(val, value);
				processedProperties.put(key, processedValue);
			} else {
				processedProperties.put(key, value);
			}
		}
		return processedProperties;
	}
}
