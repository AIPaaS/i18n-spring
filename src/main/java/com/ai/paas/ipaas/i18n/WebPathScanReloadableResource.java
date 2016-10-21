package com.ai.paas.ipaas.i18n;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class WebPathScanReloadableResource extends
		ReloadableResourceBundleMessageSource {
	private static final Logger log = LogManager
			.getLogger(WebPathScanReloadableResource.class.getName());
	private final Set<String> basePathSet = new LinkedHashSet<>(4);
	private final String PROPERTIES_SUFFIX = ".properties";

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

	@Autowired
	ServletContext servletContext;

	private void findFiles(Set<String> basePathSet) {
		Set<String> baseNames = new LinkedHashSet<>(4);
		for (String basepath : basePathSet) {
			File rootDir = new File(servletContext.getRealPath(basepath));
			if (log.isDebugEnabled()) {
				log.debug("root message file path:" + rootDir);
			}
			iterateScanDirectoryAndAddBaseNames(baseNames, rootDir);
		}
		setBasenames(baseNames.toArray(new String[baseNames.size()]));
	}

	private void iterateScanDirectoryAndAddBaseNames(Set<String> baseNames,
			File directory) {
		File[] files = directory.listFiles();
		if (log.isDebugEnabled()) {
			log.debug("start find files under root message file path:"
					+ directory.getPath() + ", found:" + files);
		}
		if (null != files) {
			for (File file : files) {
				if (file.isDirectory()) {
					iterateScanDirectoryAndAddBaseNames(baseNames, file);
				} else {
					if (file.getName().endsWith(PROPERTIES_SUFFIX)) {
						String filePath = file.getAbsolutePath()
								.replaceAll("\\\\", "/")
								.replaceAll(".properties$", "");
						filePath = filePath.substring(
								filePath.indexOf("/WEB-INF/"),
								filePath.length());
						// need to judge base file
						int pos = filePath.lastIndexOf("_");
						if (log.isDebugEnabled()) {
							log.debug(filePath + " filePath has lang profix: "
									+ pos);
						}
						if (pos > 0) {
							filePath = filePath.substring(0, pos);
							pos = filePath.lastIndexOf("_");
							if (log.isDebugEnabled()) {
								log.debug(filePath
										+ " filePath has lang profix: " + pos);
							}
							if (pos > 0)
								filePath = filePath.substring(0, pos);
						}
						// 去重，使用set吧
						baseNames.add(filePath);
						if (log.isDebugEnabled()) {
							log.debug("Added file to baseNames: " + filePath);
						}
					}
				}
			}
		}
	}

}