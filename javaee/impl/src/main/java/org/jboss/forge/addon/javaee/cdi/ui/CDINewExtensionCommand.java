/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_CDI_EXTENSIONS_PACKAGE;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a new CDI Extension
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CDINewExtensionCommand extends AbstractCDICommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Enabled?", description = "If enabled will create an entry containing this class name in the META-INF/services/javax.enterprise.inject.spi.Extension file", defaultValue = "true", type = InputType.CHECKBOX)
   private UIInput<Boolean> enable;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(enable);
   }

   @Override
   protected String getType()
   {
      return "CDI Extension";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Extension")
               .description("Creates a new CDI Extension");
   }

   @Override
   protected String calculateDefaultPackage(UIContext context)
   {
      return getSelectedProject(context).getFacet(JavaSourceFacet.class).getBasePackage() + "."
               + DEFAULT_CDI_EXTENSIONS_PACKAGE;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addInterface(Extension.class);

      String interfaceName = Extension.class.getName();
      String implName = source.getQualifiedName();
      if (enable.getValue())
      {
         FileResource<?> registryFile = getSelectedProject(context).getFacet(ResourcesFacet.class).getResource(
                  "META-INF/services/" + interfaceName);
         if (!registryFile.exists())
         {
            registryFile.createNewFile();
            registryFile.setContents(implName);
         }
         else
         {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(registryFile.getResourceInputStream()));

            boolean found = false;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
               if (line.matches("\\s*" + interfaceName + "\\s*"))
               {
                  found = true;
               }
               builder.append(line);
            }

            if (!found)
            {
               builder.append(OperatingSystemUtils.getLineSeparator()).append(implName);
               registryFile.setContents(builder.toString());
            }
         }
      }
      return source;
   }

}
