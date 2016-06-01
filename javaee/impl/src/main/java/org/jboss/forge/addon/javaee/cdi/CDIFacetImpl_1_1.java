/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;

/**
 * Implementation of {@link CDIFacet} for spec version 1.1
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDIFacetImpl_1_1 extends AbstractCDIFacetImpl<BeansDescriptor> implements CDIFacet_1_1
{
   private static final Dependency JBOSS_ANNOTATION_API_1_1 = DependencyBuilder
            .create("org.jboss.spec.javax.annotation:jboss-annotations-api_1.1_spec").setScopeType("provided");
   private static final Dependency JBOSS_ANNOTATION_API_1_2 = DependencyBuilder
            .create("org.jboss.spec.javax.annotation:jboss-annotations-api_1.2_spec").setScopeType("provided");
   private static final Dependency JAVAX_INTERCEPTOR_API = DependencyBuilder
            .create("org.jboss.spec.javax.interceptor:jboss-interceptors-api_1.1_spec").setScopeType("provided");
   private static final Dependency JAVAX_INJECT = DependencyBuilder.create("javax.inject:javax.inject:1").setScopeType(
            "provided");
   private static final Dependency JAVAX_ANNOTATION_API = DependencyBuilder.create("javax.annotation:jsr250-api:1.0");
   private static final Dependency CDI_API = DependencyBuilder.create("javax.enterprise:cdi-api:1.1").setScopeType(
            "provided");

   @Inject
   public CDIFacetImpl_1_1(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected Class<BeansDescriptor> getDescriptorType()
   {
      return BeansDescriptor.class;
   }

   @Override
   public Version getSpecVersion()
   {
      return SingleVersion.valueOf("1.1");
   }

   @Override
   protected String getDescriptorContent()
   {
      return Descriptors.create(BeansDescriptor.class).beanDiscoveryMode("all").exportAsString();
   }

   @Override
   public boolean isInstalled()
   {
      boolean installed = super.isInstalled();
      if (installed)
      {
         try
         {
            XMLResource xmlResource = (XMLResource) getConfigFile();
            installed = "1.1".equals(xmlResource.getXmlSource().getAttribute("version"));
         }
         catch (FileNotFoundException e)
         {
            installed = false;
         }
      }
      return installed;
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<>();

      result.put(CDI_API, Arrays.asList(CDI_API, JAVAEE7));
      result.put(JAVAX_ANNOTATION_API,
               Arrays.asList(JAVAX_ANNOTATION_API, JBOSS_ANNOTATION_API_1_1, JBOSS_ANNOTATION_API_1_2, JAVAEE7));
      result.put(JAVAX_INJECT, Arrays.asList(JAVAX_INJECT, JAVAEE7));
      result.put(JAVAX_INTERCEPTOR_API, Arrays.asList(JAVAX_INTERCEPTOR_API, JAVAEE7));

      return result;
   }

}
