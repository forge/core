/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

/**
 * @author Kevin Pollet
 */
public class ValidationFacetImpl extends AbstractJavaEEFacet implements ValidationFacet
{
   private final Dependency JAVAX_VALIDATION_API = DependencyBuilder
            .create("javax.validation:validation-api").setScopeType("provided");

   @Inject
   public ValidationFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "Bean Validation";
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         FileResource<?> descriptor = getConfigFile();
         if (!descriptor.exists())
         {
            createDefaultConfig(descriptor);
         }
      }
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists() && super.isInstalled();
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("1.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      return Collections.singletonMap(JAVAX_VALIDATION_API, Arrays.asList(JAVAX_VALIDATION_API, JAVAEE7));
   }

   /*
    * Facet methods
    */

   @Override
   public ValidationConfigurationDescriptor getConfig()
   {
      DescriptorImporter<ValidationConfigurationDescriptor> importer = Descriptors
               .importAs(ValidationConfigurationDescriptor.class);
      final FileResource<?> configFile = getConfigFile();
      if (!configFile.exists())
      {
         createDefaultConfig(configFile);
      }
      ValidationConfigurationDescriptor descriptor = importer.fromStream(configFile.getResourceInputStream());
      return descriptor;
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      ResourcesFacet resources = getFaceted().getFacet(ResourcesFacet.class);
      return resources.getResource("META-INF" + File.separator + "validation.xml");
   }

   @Override
   public void saveConfig(final ValidationConfigurationDescriptor descriptor)
   {
      final FileResource<?> fileResource = getConfigFile();
      fileResource.createNewFile();
      fileResource.setContents(descriptor.exportAsString());
   }

   private void createDefaultConfig(FileResource<?> descriptor)
   {
      ValidationConfigurationDescriptor descriptorContents = Descriptors
               .create(ValidationConfigurationDescriptor.class).version("1.1");
      descriptor.setContents(descriptorContents.exportAsString());
   }
}
