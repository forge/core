/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.facesconfig20.WebFacesConfigDescriptor;

/**
 * Implementation of the {@link FacesFacet_2_0} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FacesFacetImpl_2_0 extends AbstractFacesFacetImpl<WebFacesConfigDescriptor> implements FacesFacet_2_0
{
   public static final Dependency JAVAEE6_FACES = DependencyBuilder
            .create("org.jboss.spec.javax.faces:jboss-jsf-api_2.0_spec:1.0.0.Final").setScopeType("provided");

   @Inject
   public FacesFacetImpl_2_0(DependencyInstaller installer)
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
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      result.put(JAVAEE6_FACES, Arrays.asList(JAVAEE6_FACES));
      return result;
   }

   @Override
   public void saveConfig(WebFacesConfigDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   protected Class<WebFacesConfigDescriptor> getDescriptorClass()
   {
      return WebFacesConfigDescriptor.class;
   }

   @Override
   protected void createDefaultConfig(FileResource<?> descriptor)
   {
      WebFacesConfigDescriptor descriptorContents = Descriptors.create(WebFacesConfigDescriptor.class)
               .version("2.0");
      descriptor.setContents(descriptorContents.exportAsString());
   }
}
