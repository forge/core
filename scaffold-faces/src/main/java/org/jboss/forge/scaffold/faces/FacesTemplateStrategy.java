/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces;

import java.util.Arrays;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.forge.shell.util.Streams;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class FacesTemplateStrategy implements TemplateStrategy
{
   private static final String SCAFFOLD_FORGE_TEMPLATE = "/resources/scaffold/pageTemplate.xhtml";

   private final Project project;

   public FacesTemplateStrategy(final Project project)
   {
      this.project = project;
   }

   @Override
   public boolean compatibleWith(final Resource<?> template)
   {
      String contents = Streams.toString(template.getResourceInputStream());
      for (String section : Arrays.asList("main")) {
         if (!contents.matches(".*:\\s*insert\\s+name\\s*=\\s*\"" + section + "\".*"))
         {
            return false;
         }
      }
      return true;
   }

   @Override
   public String getReferencePath(final Resource<?> template)
   {
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      for (DirectoryResource dir : web.getWebRootDirectories()) {
         if (ResourceUtil.isChildOf(dir, template))
         {
            String relativePath = template.getFullyQualifiedName().substring(dir.getFullyQualifiedName().length());
            return relativePath;
         }
      }
      throw new IllegalArgumentException("Not a valid template resource for this scaffold.");
   }

   @Override
   public FileResource<?> getDefaultTemplate()
   {
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      return web.getWebResource(SCAFFOLD_FORGE_TEMPLATE);
   }

}
