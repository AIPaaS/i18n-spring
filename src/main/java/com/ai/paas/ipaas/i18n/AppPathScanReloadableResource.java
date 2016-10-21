package com.ai.paas.ipaas.i18n;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class AppPathScanReloadableResource extends
		ReloadableResourceBundleMessageSource {
	private static final Logger log = LogManager
			.getLogger(AppPathScanReloadableResource.class.getName());
	private final Set<String> basePathSet = new LinkedHashSet<>(4);
	private final String PROPERTIES_SUFFIX = ".properties";
	private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
			this.getClass().getClassLoader());

	public void setBasepaths(String... basepaths) {
		basePathSet.clear();
		addBasepaths(basepaths);
	}

	public void addBasepaths(String... basepaths) {
		if (!ObjectUtils.isEmpty(basepaths)) {
			for (String basepath : basepaths) {
				Assert.hasText(basepath, "Basepath must not be empty");
				if (log.isDebugEnabled()) {
					log.debug("Add the base message file path:"
							+ basepath.trim());
				}
				basePathSet.add(basepath.trim());
			}
		}
		// 这里开始循环查找
		findFiles(basePathSet);
	}

	private void findFiles(Set<String> basePathSet) {
		Set<String> baseNames = new LinkedHashSet<>(4);
		for (String basepath : basePathSet) {
			try {
				if (!basepath.endsWith("/")) {
					basepath = basepath + "/**/*";
				}
				Resource[] resources = resolver.getResources(basepath
						+ PROPERTIES_SUFFIX);
				for (Resource resource : resources) {
					String sourcePath = resource.getURI().toString()
							.replace(PROPERTIES_SUFFIX, "");
					if (log.isDebugEnabled()) {
						log.debug("I18N Find properties file:" + sourcePath);
					}
					int pos = sourcePath.lastIndexOf("_");
					if (log.isDebugEnabled()) {
						log.debug(sourcePath + " filePath has lang profix: "
								+ pos);
					}
					if (pos > 0) {
						sourcePath = sourcePath.substring(0, pos);
						pos = sourcePath.lastIndexOf("_");
						if (log.isDebugEnabled()) {
							log.debug(sourcePath
									+ " filePath has lang profix: " + pos);
						}
						if (pos > 0)
							sourcePath = sourcePath.substring(0, pos);
					}
					baseNames.add(sourcePath);
				}
				resources = null;
			} catch (IOException e) {

			}
		}
		setBasenames(baseNames.toArray(new String[baseNames.size()]));
	}

}