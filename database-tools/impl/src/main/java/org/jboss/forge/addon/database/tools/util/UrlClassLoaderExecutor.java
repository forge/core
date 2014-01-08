package org.jboss.forge.addon.database.tools.util;

import java.net.URL;
import java.net.URLClassLoader;

public class UrlClassLoaderExecutor {

	public static void execute(URL[] urls, Runnable runnable) {
		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			URLClassLoader newClassLoader = new URLClassLoader(urls, savedClassLoader);
			Thread.currentThread().setContextClassLoader(newClassLoader);
			runnable.run();
		} finally {
			Thread.currentThread().setContextClassLoader(savedClassLoader);
		}
	}
	
}
