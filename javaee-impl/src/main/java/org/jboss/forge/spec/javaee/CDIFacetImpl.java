/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.packaging.events.PackagingChanged;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.cdi")
@RequiresFacet({ ResourceFacet.class, PackagingFacet.class })
public class CDIFacetImpl extends BaseFacet implements CDIFacet
{
   @Inject
   private Shell shell;

   public void updateConfigLocation(@Observes final PackagingChanged event)
   {
      Project project = event.getProject();
      if (project.hasFacet(CDIFacetImpl.class))
      {
         PackagingType oldType = event.getOldPackagingType();
         PackagingType newType = event.getNewPackagingType();
         shell.printlnVerbose("Packaging type change detected; moving beans.xml " +
                  "from [" + oldType + "] to [" + newType + "]");

         // FIXME this is broken...
         FileResource<?> configFile = getConfigFile(project, oldType);

         BeansDescriptor config = getConfig(project, configFile);
         saveConfig(project, config);

         configFile.delete();
      }
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      return getConfigFile(project);
   }

   private FileResource<?> getConfigFile(final Project project)
   {
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      return getConfigFile(project, packaging.getPackagingType());
   }

   private FileResource<?> getConfigFile(final Project project, final PackagingType type)
   {
      if (PackagingType.WAR.equals(type))
      {
         DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
         return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "beans.xml");
      }
      else
      {
         if (!PackagingType.JAR.equals(type))
         {
            printPackTypeWarning(project, type);
         }
         DirectoryResource root = project.getFacet(ResourceFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "beans.xml");
      }
   }

   private void printPackTypeWarning(final Project project, final PackagingType type)
   {
      shell.print(ShellColor.RED, "***WARNING*** ");
      shell.println("Unsupported packaging type detected [" + type.getType()
               + "], using default beans.xml location ["
               + getConfigFile(project, PackagingType.JAR).getFullyQualifiedName() + "]");
   }

   @Override
   public BeansDescriptor getConfig()
   {
      return getConfig(project, getConfigFile(project));
   }

   private BeansDescriptor getConfig(final Project project, final FileResource<?> file)
   {
      DescriptorImporter<BeansDescriptor> importer = Descriptors.importAs(BeansDescriptor.class);
      BeansDescriptor descriptor = importer.from(file.getResourceInputStream());
      return descriptor;
   }

   @Override
   public void saveConfig(final BeansDescriptor model)
   {
      saveConfig(project, model);
   }

   private void saveConfig(final Project project, final BeansDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile(project).setContents(output);
   }

   /*
    * Facet Methods
    */
   @Override
   public boolean isInstalled()
   {
      return getConfigFile(project).exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         PackagingType type = project.getFacet(PackagingFacet.class).getPackagingType();
         if (!PackagingType.JAR.equals(type) && !PackagingType.WAR.equals(type))
         {
            printPackTypeWarning(project, type);
         }

         FileResource<?> descriptor = getConfigFile(project);
         if (!descriptor.createNewFile())
         {
            throw new RuntimeException("Failed to create required [" + descriptor.getFullyQualifiedName() + "]");
         }

         descriptor.setContents(getClass()
                     .getResourceAsStream("/org/jboss/forge/web/beans.xml"));

      }
      return true;
   }
}
