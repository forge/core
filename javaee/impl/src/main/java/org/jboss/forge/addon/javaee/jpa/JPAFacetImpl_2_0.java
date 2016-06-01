/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JPAFacetImpl_2_0 extends AbstractJPAFacetImpl<PersistenceDescriptor>implements JPAFacet_2_0
{
   private final Dependency JAVAX_PERSISTENCE = DependencyBuilder
            .create("org.hibernate.javax.persistence:hibernate-jpa-2.0-api").setScopeType("provided");

   @Inject
   public JPAFacetImpl_2_0(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("2.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      return Collections.singletonMap(JAVAX_PERSISTENCE, Arrays.asList(JAVAX_PERSISTENCE, JAVAEE6));
   }

   @Override
   public PersistenceDescriptor getConfig()
   {
      DescriptorImporter<PersistenceDescriptor> importer = Descriptors.importAs(PersistenceDescriptor.class);
      final FileResource<?> configFile = getConfigFile();
      if (!configFile.exists())
      {
         createDefaultConfig(configFile);
      }
      PersistenceDescriptor descriptor = importer.fromStream(configFile.getResourceInputStream());
      return descriptor;
   }

   @Override
   protected void createDefaultConfig(FileResource<?> descriptor)
   {
      PersistenceDescriptor descriptorContents = Descriptors.create(PersistenceDescriptor.class)
               .version("2.0");
      saveConfig(descriptorContents);
   }

   @Override
   public void saveConfig(final PersistenceDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }

}
