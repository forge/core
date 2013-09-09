/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.facesconfig20.WebFacesConfigDescriptor;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FacesFacetImpl_2_0 extends AbstractFacesFacetImpl implements FacesFacet_2_0
{
   @Inject
   public FacesFacetImpl_2_0(DependencyInstaller installer)
   {
      super(installer);
   }

   public static final Dependency JAVAEE6_FACES = DependencyBuilder
            .create("org.jboss.spec.javax.faces:jboss-jsf-api_2.0_spec").setScopeType("provided");

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("2.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<Dependency, List<Dependency>>();
      result.put(JAVAEE6_FACES, Arrays.asList(JAVAEE6_FACES));
      return result;
   }

   @Override
   public WebFacesConfigDescriptor getConfig()
   {
      WebFacesConfigDescriptor descriptor;
      FileResource<?> configFile = getConfigFile();
      if (configFile.exists())
      {
         DescriptorImporter<WebFacesConfigDescriptor> importer = Descriptors.importAs(WebFacesConfigDescriptor.class);
         descriptor = importer.fromStream(configFile.getResourceInputStream());
      }
      else
      {
         descriptor = Descriptors.create(WebFacesConfigDescriptor.class);
      }
      return descriptor;
   }

   @Override
   public void saveConfig(WebFacesConfigDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }
}
