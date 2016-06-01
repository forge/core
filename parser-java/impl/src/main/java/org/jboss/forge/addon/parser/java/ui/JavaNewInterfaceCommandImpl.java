/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.roaster.model.source.InterfaceCapableSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaNewInterfaceCommandImpl extends AbstractJavaSourceCommand<JavaInterfaceSource> implements
         JavaNewInterfaceCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected Class<JavaInterfaceSource> getSourceType()
   {
      return JavaInterfaceSource.class;
   }

   @Override
   protected String getType()
   {
      return "Interface";
   }

   /**
    * Interfaces don't require overriding method declarations
    */
   @Override
   protected void implementInterface(InterfaceCapableSource<?> source, Iterable<String> value, JavaSourceFacet facet)
   {
      for (String type : value)
      {
         source.addInterface(type);
      }
   }
}
