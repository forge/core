/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaNewClassCommandImpl extends AbstractJavaSourceCommand<JavaClassSource>implements JavaNewClassCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String getType()
   {
      return "Class";
   }
}
