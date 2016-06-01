/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class JavaNewExceptionCommandImpl extends AbstractJavaSourceCommand<JavaClassSource>implements
         JavaNewExceptionCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.setSuperType(RuntimeException.class);
      source.addMethod().setPublic().setConstructor(true).setBody("super();");
      source.addMethod().setPublic().setConstructor(true).setParameters("String message").setBody("super(message);");

      return source;
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String getType()
   {
      return "Exception";
   }
}
