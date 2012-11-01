package org.jboss.forge.container.modules;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.environment.se.discovery.ImmutableBeanDeploymentArchive;
import org.jboss.weld.environment.se.discovery.url.ClasspathScanningException;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scan the classloader
 *
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 * @author Pete Muir
 * @author Peter Royle
 */
public class ModularURLScanner {

    private static final Logger log = LoggerFactory.getLogger(ModularURLScanner.class);
    private final String[] resources;
    private final ResourceLoader resourceLoader;
    private final Bootstrap bootstrap;

    public ModularURLScanner(ResourceLoader resourceLoader, Bootstrap bootstrap, String... resources) {
        this.resources = resources;
        this.resourceLoader = resourceLoader;
        this.bootstrap = bootstrap;
    }

    public BeanDeploymentArchive scan() {
        List<String> discoveredClasses = new ArrayList<String>();
        List<URL> discoveredBeanXmlUrls = new ArrayList<URL>();
        Collection<String> paths = new ArrayList<String>();
        for (String resourceName : resources) {
            // grab all the URLs for this resource
            Collection<URL> urlEnum = resourceLoader.getResources(resourceName);
            for (URL url : urlEnum) {

                String urlPath = url.toExternalForm();

                // determin resource type (eg: jar, file, bundle)
                String urlType = "file";
                int colonIndex = urlPath.indexOf(":");
                if (colonIndex != -1) {
                    urlType = urlPath.substring(0, colonIndex);
                }

                // Extra built-in support for simple file-based resources
                if ("file".equals(urlType) || "jar".equals(urlType)) {
                    // switch to using getPath() instead of toExternalForm()
                    urlPath = url.getPath();

                    if (urlPath.indexOf('!') > 0) {
                        urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                    } else {
                        // hack for /META-INF/beans.xml
                        File dirOrArchive = new File(urlPath);
                        if ((resourceName != null) && (resourceName.lastIndexOf('/') > 0)) {
                            dirOrArchive = dirOrArchive.getParentFile();
                        }
                        urlPath = dirOrArchive.getParent();
                    }
                }

                try {
                    urlPath = URLDecoder.decode(urlPath, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new ClasspathScanningException("Error decoding URL using UTF-8");
                }

                log.debug("URL Type: " + urlType);

                paths.add(urlPath);
            }

            ModularFileSystemURLHandler handler = new ModularFileSystemURLHandler(resourceLoader);
            handler.handle(paths, discoveredClasses, discoveredBeanXmlUrls);
        }
        return new ImmutableBeanDeploymentArchive("classpath", discoveredClasses, bootstrap.parse(discoveredBeanXmlUrls));
    }

}
