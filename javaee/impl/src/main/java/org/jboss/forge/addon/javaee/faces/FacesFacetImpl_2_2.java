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
import org.jboss.shrinkwrap.descriptor.api.facesconfig22.WebFacesConfigDescriptor;

/**
 * Facets for the JSF 2.2 Implementation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class FacesFacetImpl_2_2 extends AbstractFacesFacetImpl<WebFacesConfigDescriptor> implements FacesFacet_2_2
{

   public static final Dependency FACES_2_2 = DependencyBuilder
            .create("javax.faces:javax.faces-api:2.2").setScopeType("provided");

   @Inject
   public FacesFacetImpl_2_2(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("2.2");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();
      result.put(FACES_2_2, Arrays.asList(FACES_2_2, JAVAEE7));
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
               .version("2.2");
      descriptor.setContents(descriptorContents.exportAsString());
   }
}
