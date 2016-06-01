/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.roaster.model.source.JavaEnumSource;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaNewEnumCommandImpl extends AbstractJavaSourceCommand<JavaEnumSource>implements JavaNewEnumCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected Class<JavaEnumSource> getSourceType()
   {
      return JavaEnumSource.class;
   }

   @Override
   protected String getType()
   {
      return "Enum";
   }
}
