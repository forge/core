/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.metawidget.forge.validation;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.FacetInstalled;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.shrinkwrap.descriptor.spi.Node;
import org.metawidget.forge.MetawidgetScaffold;

/**
 * Handles configuration of metawidget for bean validation if both are detected in a project // TODO this should also be
 * available via a command: 'metawidget setup-validation'
 * 
 * @author Kevin Pollet
 */
public class MetawidgetValidationConfigurator
{
   private final Project project;
   private final ShellPrompt prompt;

   @Inject
   public MetawidgetValidationConfigurator(final Project project, final ShellPrompt prompt)
   {
      this.project = project;
      this.prompt = prompt;
   }

   // TODO expose this as a declarative plugin command (for use on existing/pre configured projects)
   public void addValidationConfiguration(@Observes final FacetInstalled event)
   {
      if (((event.getFacet() instanceof MetawidgetScaffold) && project.hasFacet(ValidationFacet.class))
               || ((event.getFacet() instanceof ValidationFacet) && project.hasFacet(MetawidgetScaffold.class)))
      {
         final FileResource<?> configFile = getMetawidgetConfigurationFile();
         if (configFile.exists())
         {
            final Node root = XMLParser.parse(configFile.getResourceInputStream());
            final Node array = root.getOrCreate("htmlMetawidget")
                        .attribute("xmlns", "java:org.metawidget.faces.component.html")
                        .getOrCreate("inspector")
                        .getOrCreate("compositeInspector")
                        .attribute("xmlns", "java:org.metawidget.inspector.composite")
                        .attribute("config", "CompositeInspectorConfig")
                        .getOrCreate("inspectors")
                        .getOrCreate("array");

            final Node beanValidationInspector = array.getSingle("beanValidationInspector");

            if ((beanValidationInspector == null) && prompt
                     .promptBoolean("MetaWidget scaffold detected would you like to add validation configuration?"))
            {
               // if configuration already exists it is overwritten
               addValidationConfigurationTo(array);

               // saves metawidget configuration file
               configFile.setContents(XMLParser.toXMLString(root));
            }
         }
      }
   }

   private FileResource<?> getMetawidgetConfigurationFile()
   {
      final WebResourceFacet facet = project.getFacet(WebResourceFacet.class);
      return facet.getWebResource("WEB-INF" + File.separator + "metawidget.xml");
   }

   private void addValidationConfigurationTo(final Node node)
   {
      node.getOrCreate("beanValidationInspector")
                .attribute("xmlns", "java:org.metawidget.inspector.beanvalidation")
                .attribute("config", "org.metawidget.inspector.impl.BaseObjectInspectorConfig")
                .getOrCreate("propertyStyle")
                .getOrCreate("javaBeanPropertyStyle")
                .attribute("xmlns", "java:org.metawidget.inspector.impl.propertystyle.javabean")
                .attribute("config", "JavaBeanPropertyStyleConfig")
                .getOrCreate("privateFieldConvention")
                .getOrCreate("format")
                .text("{0}");
   }
}
