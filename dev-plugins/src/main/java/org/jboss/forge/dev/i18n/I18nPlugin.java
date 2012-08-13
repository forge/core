/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.i18n;

import javax.inject.Inject;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.PropertiesFileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceException;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.FacesFacet;

/**
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 * 
 */

@Alias("i18n")
@Help("Enables i18n support in a project. Deals with property resource bundle files.")
@RequiresProject
public class I18nPlugin implements Plugin
{

   @Inject
   @Current
   PropertiesFileResource propertiesFileResource;

   @Inject
   private Project project;

   @Inject
   private Shell shell;

   @SetupCommand(help = "Configures a bundle for i18n operations")
   public void setupBundle(
            final PipeOut out,
            @Option(name = "bundleName", description = "Bundle Name", defaultValue = "messages") final String bundleName)
            throws Exception
   {
      String fullBundleFile = bundleName.endsWith(".properties") ? bundleName : bundleName + ".properties";
      DirectoryResource resourceFolder = project.getFacet(ResourceFacet.class).getResourceFolder();

      PropertiesFileResource bundleFile = resourceFolder.getChildOfType(PropertiesFileResource.class, fullBundleFile);
      if (!bundleFile.exists())
      {
         bundleFile.createNewFile();
      }

      shell.setCurrentResource(bundleFile);

      out.println("Bundle " + bundleFile + " has been created !");

      if (project.hasFacet(FacesFacet.class))
      {
         shell.execute("i18n faces-setup");
      }
   }

   /**
    * Adds an entry to the current bundle.
    * 
    * TODO: Add a completer for current bundle keys
    * 
    * @param key
    * @param value
    */
   @Command(value = "put", help = "Adds or modifies an entry in this resource bundle")
   public void putProperty(@Option(name = "key", required = true, completer = BundleCompleter.class) String key,
            @Option(name = "value") String value,
            @Option(name = "locale") String locale)
   {
      assertPropertiesInContext();
      if (locale == null)
      {
         propertiesFileResource.putProperty(key, value);
      }
      else
      {
         String bundleName = getBaseBundleName(propertiesFileResource.getName()) + "_" + locale + ".properties";
         PropertiesFileResource newFileResource = getOrCreate(bundleName);
         newFileResource.putProperty(key, value);
         shell.setCurrentResource(newFileResource);
      }
   }

   /**
    * Removes an entry to the current bundle.
    * 
    * @param key
    * @param value
    */
   @Command(value = "remove", help = "Removes an entry in this resource bundle and all associated bundle locales")
   public void removeProperty(
            @Option(name = "key", required = true, completer = BundleCompleter.class) String key,
            @Option(name = "currentOnly", flagOnly = true, description = "Delete only from current bundle") boolean currentOnly)
   {
      if (currentOnly)
      {
         propertiesFileResource.removeProperty(key);
      }
      else
      {
         String baseName = "messages";
         if (propertiesFileResource != null)
         {
            baseName = getBaseBundleName(propertiesFileResource.getName());
         }
         final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
         final BundleBaseNameResourceFilter filter = new BundleBaseNameResourceFilter(baseName);
         for (DirectoryResource resourceFolder : resourceFacet.getResourceFolders())
         {
            for (Resource<?> r : resourceFolder.listResources(filter))
            {
               shell.println("Removing key \"" + key + "\" on bundle " + r.getFullyQualifiedName());
               ((PropertiesFileResource) r).removeProperty(key);
            }
         }

      }
   }

   @Command(value = "get", help = "Adds or modifies an entry in this resource bundle")
   public void getProperty(@Option(name = "key", required = true, completer = BundleCompleter.class) String key,
            @Option(name = "locale") String locale)
   {
      assertPropertiesInContext();
      String propertyValue = null;
      if (locale == null)
      {
         propertyValue = propertiesFileResource.getProperty(key);
      }
      else
      {
         String bundleName = getBaseBundleName(propertiesFileResource.getName()) + "_" + locale + ".properties";
         PropertiesFileResource newFilePropertiesResource = getOrCreate(bundleName);
         propertyValue = newFilePropertiesResource.getProperty(key);
      }
      shell.println(propertyValue);
   }

   @Command(value = "add-locale", help = "Adds another locale in this resource bundle")
   public void addLocale(@Option(name = "locale", required = true) String locale)
   {
      String baseName = "messages";
      if (propertiesFileResource != null)
      {
         baseName = getBaseBundleName(propertiesFileResource.getName());
      }

      final String baseBundleName = (baseName + "_" + locale + ".properties");
      PropertiesFileResource newFileResource = getOrCreate(baseBundleName);
      shell.setCurrentResource(newFileResource);
   }

   @Command(value = "faces-setup", help = "Setup this bundle as a ResourceBundle in faces-config.xml")
   public void setupFaces(
            @Option(name = "var", description = "The name by which this ResourceBundle instance is retrieved by a call to Application.getResourceBundle()", required = true, defaultValue = "msg") String varName)
   {
      assertPropertiesInContext();
      FacesFacet facesFacet = project.getFacet(FacesFacet.class);
      FileResource<?> configFile = facesFacet.getConfigFile();
      String baseBundleName = getBaseBundleName(propertiesFileResource.getName());

      Node facesConfig = XMLParser.parse(configFile.getResourceInputStream());

      Node applicationNode = facesConfig.getOrCreate("application");

      Node resourceBundleNode = applicationNode.getOrCreate("resource-bundle");
      resourceBundleNode.getOrCreate("base-name").text(baseBundleName);
      resourceBundleNode.getOrCreate("var").text(varName);

      configFile.setContents(XMLParser.toXMLInputStream(facesConfig));
      shell.println("Faces config has been updated.");
   }

   /**
    * Gets another file resource. Creates a file in case it does not exist
    * 
    * @param bundleName
    * @return
    */
   protected PropertiesFileResource getOrCreate(final String bundleName)
   {
      final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
      final BundleBaseNameResourceFilter filter = new BundleBaseNameResourceFilter(bundleName);
      PropertiesFileResource newFileResource = null;
      for (DirectoryResource directoryResource : resourceFacet.getResourceFolders())
      {
         for (Resource<?> resource : directoryResource.listResources(filter))
         {
            newFileResource = (PropertiesFileResource) resource;
            // Using the first resource found
            break;
         }
      }
      if (newFileResource == null)
      {
         newFileResource = resourceFacet.getResourceFolder().getChildOfType(PropertiesFileResource.class,
                  bundleName);
         if (!newFileResource.exists())
         {
            newFileResource.createNewFile();
         }
      }
      return newFileResource;
   }

   /**
    * Returns the base name of the bundle.
    * 
    * Eg: messages_pt_BR.properties returns just messages
    * 
    * @return
    */
   static String getBaseBundleName(final String fileName)
   {
      String baseName = fileName.replace(".properties", "");
      // TODO: Check whether underscored files are used. Eg: my_messages_pt_BR.properties
      int idxUnderscore = baseName.indexOf("_");
      if (idxUnderscore > -1)
      {
         baseName = baseName.substring(0, idxUnderscore);
      }
      return baseName;
   }

   private class BundleBaseNameResourceFilter implements ResourceFilter
   {
      private String fileName;

      public BundleBaseNameResourceFilter(String fileName)
      {
         this.fileName = fileName;
      }

      @Override
      public boolean accept(Resource<?> resource)
      {
         return (resource instanceof PropertiesFileResource && resource.getName().startsWith(fileName));
      }
   }

   private void assertPropertiesInContext()
   {
      if (propertiesFileResource == null)
      {
         throw new ResourceException("No bundle informed or in context");
      }

   }
}
