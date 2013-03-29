package org.jboss.forge.parser.java;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.parser.java.binary.WrappedJavaClass;
import org.jboss.forge.parser.java.binary.WrappedJavaEnum;
import org.jboss.forge.parser.java.binary.WrappedJavaInterface;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.events.AddedDependencies;
import org.jboss.forge.project.dependencies.events.RemovedDependencies;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaBinaryFacet;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({DependencyFacet.class})
public class JavaBinaryFacetImpl extends BaseFacet implements JavaBinaryFacet {

	private @Inject DependencyResolver dependencyResolver;
	
	private ClassLoader classLoader;

	@Override
	public boolean install() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}


	@Override
	public JavaSource<? extends JavaSource<?>> find(String qualifiedName) {
		Class<?> clzz = null;
		try {
			clzz = getClassLoader() == null ? null : getClassLoader().loadClass(qualifiedName);
		} catch (ClassNotFoundException e) {
		}
		return wrap(clzz);
	}
	


	@Override
	public JavaSource<? extends JavaSource<?>> find(String pkg, String simpleName) {
		StringBuilder b = new StringBuilder();
		b.append(pkg).append('.').append(simpleName);
		return find(b.toString());
	}

	@Override
	public JavaSource<? extends JavaSource<?>> find(String[] pkgs, String simpleName) {
		JavaSource<? extends JavaSource<?>> result = null;
		for(int i = 0; i < pkgs.length && result == null; i++) {
			result = find(pkgs[i], simpleName);
		}
		return result;
	}

	/**
	 * removes dependencies from the index.
	 * @param dependencies
	 */
	void removeDependencies(@Observes RemovedDependencies dependencies) {
		clearCache();
	}

	/**
	 * wrap the java class by forge java model: {@link JavaInterface}, {@link JavaEnum} or {@link JavaClass}.
	 * @param clzz
	 * @return
	 */
	private JavaSource<? extends JavaSource<?>> wrap(Class<?> clzz) {
		JavaSource<? extends JavaSource<?>> result = null;
		
		if(clzz != null) {
			if(clzz.isInterface()) {
				result = new WrappedJavaInterface(clzz);
			} else if(clzz.isEnum()) {
				result = new WrappedJavaEnum(clzz);
			} else {
				result = new WrappedJavaClass(clzz);
			}
		}
		return result;
	}


	void addDependencies(@Observes AddedDependencies dependencies) {
		clearCache();
	}
	private ClassLoader getClassLoader() {
		if(classLoader == null) {
			classLoader = createClassLoader();
		}
		return classLoader;
	}
	
	/**
	 * load the projects dependencies into a single class loader
	 * @return
	 */
	private ClassLoader createClassLoader() {
		List<URL> urls = getDependenciesAsURLs();
		return urls.size() > 0 ? newLoader(urls) : null;
	}

	private URLClassLoader newLoader(List<URL> urls) {
		return new URLClassLoader(urls.toArray(new URL[urls.size()]));
	}

	/**
	 * build list of dependenices as url.
	 * @return
	 */
	private List<URL> getDependenciesAsURLs() {
		List<URL> dependencyUrls = new ArrayList<URL>();
		DependencyFacet dependencies = getProject().getFacet(DependencyFacet.class);
		for(Dependency dependency: dependencies.getDependencies()) {
			System.out.println("dependency: " + dependency.toCoordinates());
			List<DependencyResource> resources = dependencyResolver.resolveArtifacts(dependency);
			for(DependencyResource resource: resources) {
				System.out.println("resource: " + resource.getName());
				java.io.File file = resource.getUnderlyingResourceObject();
				URL jarUrl = createJarUrl(file);
				if(jarUrl != null) {
					dependencyUrls.add(jarUrl);
				}
			}
		}
		return dependencyUrls;
	}
	private URL createJarUrl(java.io.File file) {
		URL jarUrl = null;
		try {
			jarUrl = new URL("file://" + file.getAbsolutePath());
		} catch (MalformedURLException e) {
		}
		return jarUrl;
	}

	private void clearCache() {
		// force the class loader to be rebuilt on next resolve request
		// TODO: performance analysis and compare alternatives
		if(classLoader != null) {
			classLoader = null;
		}
	}

}
