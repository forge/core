/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServletFacetImpl_3_1 extends AbstractServletFacet<WebAppDescriptor> implements ServletFacet_3_1
{
   private static final Dependency JAVAX_SERVLET_API = DependencyBuilder
            .create("javax.servlet:javax.servlet-api:3.1.0").setScopeType("provided");

   @Inject
   public ServletFacetImpl_3_1(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("3.1");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> map = new LinkedHashMap<>();
      map.put(JAVAX_SERVLET_API, Arrays.asList(JAVAX_SERVLET_API, JAVAEE7));
      return map;
   }

   /*
    * Facet Methods
    */
   @Override
   public WebAppDescriptor getConfig()
   {
      FileResource<?> configFile = getConfigFile();
      WebAppDescriptor descriptor;
      if (configFile.exists())
      {

         DescriptorImporter<WebAppDescriptor> importer = Descriptors.importAs(WebAppDescriptor.class);
         InputStream inputStream = configFile.getResourceInputStream();
         try
         {
            descriptor = importer.fromStream(inputStream);
         }
         finally
         {
            Streams.closeQuietly(inputStream);
         }
      }
      else
      {
         descriptor = Descriptors.create(WebAppDescriptor.class);
         String projectName = getFaceted().getFacet(MetadataFacet.class).getProjectName();
         WebAppDescriptor unit = descriptor
                  .version("3.1")
                  .displayName(projectName)
                  .createSessionConfig()
                  .sessionTimeout(30).up();
         // FORGE-657
         unit.createMimeMapping().extension("ico").mimeType("image/x-icon");
      }
      return descriptor;
   }

   @Override
   public void saveConfig(final WebAppDescriptor descriptor)
   {
      FileResource<?> configFile = getConfigFile();
      String output = descriptor.exportAsString();
      configFile.setContents(output);
   }

}