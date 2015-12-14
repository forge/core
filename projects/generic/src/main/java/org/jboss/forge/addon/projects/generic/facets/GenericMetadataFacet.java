/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.generic.facets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.generic.GenericProjectProvider;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Generic implementation for {@link MetadataFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericMetadataFacet extends AbstractFacet<Project> implements MetadataFacet
{
   /**
    * The file that contains the metadata
    */
   public static final String PROJECT_METADATA_FILE_NAME = ".forge-metadata.properties";

   private FileResource<?> metadataFile;

   @Override
   public String getProjectName()
   {
      return getMetadata().getProperty("name");
   }

   @Override
   public MetadataFacet setProjectName(String name)
   {
      Properties metadata = getMetadata();
      metadata.setProperty("name", name);
      store(metadata);
      return this;
   }

   @Override
   public String getTopLevelPackage()
   {
      return getProjectGroupName();
   }

   @Override
   public String getProjectGroupName()
   {
      return getMetadata().getProperty("groupName");
   }

   @Override
   public MetadataFacet setTopLevelPackage(String groupId)
   {
      return setProjectGroupName(groupId);
   }

   @Override
   public MetadataFacet setProjectGroupName(String groupId)
   {
      Properties metadata = getMetadata();
      metadata.setProperty("groupName", groupId);
      store(metadata);
      return this;
   }

   @Override
   public String getProjectVersion()
   {
      return getMetadata().getProperty("version");
   }

   @Override
   public MetadataFacet setProjectVersion(String version)
   {
      Properties metadata = getMetadata();
      metadata.setProperty("version", version);
      store(metadata);
      return this;
   }

   @Override
   public Dependency getOutputDependency()
   {
      return null;
   }

   @Override
   public Map<String, String> getEffectiveProperties()
   {
      return getDirectProperties();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Map<String, String> getDirectProperties()
   {
      Map map = getMetadata();
      return map;
   }

   @Override
   public String getEffectiveProperty(String name)
   {
      return getEffectiveProperties().get(name);
   }

   @Override
   public String getDirectProperty(String name)
   {
      return getDirectProperties().get(name);
   }

   @Override
   public MetadataFacet setDirectProperty(String name, String value)
   {
      Properties metadata = getMetadata();
      metadata.setProperty(name, value);
      store(metadata);
      return this;
   }

   @Override
   public String removeDirectProperty(String name)
   {
      Properties metadata = getMetadata();
      String value = (String) metadata.remove(name);
      store(metadata);
      return value;
   }

   @Override
   public ProjectProvider getProjectProvider()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), GenericProjectProvider.class).get();
   }

   @Override
   public boolean isValid()
   {
      return isInstalled();
   }

   private void store(Properties props)
   {
      try (OutputStream os = getMetadataFile().getResourceOutputStream())
      {
         props.store(os, null);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error while updating metadata", e);
      }
   }

   private Properties getMetadata()
   {
      Properties props = new Properties();
      try (InputStream is = getMetadataFile().getResourceInputStream())
      {
         props.load(is);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error while loading metadata", e);
      }
      return props;
   }

   public FileResource<?> getMetadataFile()
   {
      if (metadataFile == null)
      {
         metadataFile = getFaceted().getRoot().getChild(PROJECT_METADATA_FILE_NAME)
                  .reify(FileResource.class);
      }
      return metadataFile;
   }

   @Override
   public boolean install()
   {
      FileResource<?> metadataFile = getMetadataFile();
      if (!metadataFile.exists())
      {
         metadataFile.createNewFile();
      }
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      return getMetadataFile().exists();
   }
}