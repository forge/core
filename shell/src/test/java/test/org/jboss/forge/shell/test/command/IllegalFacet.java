/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.shell.test.command;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;

@Alias("FORGE-929")
public class IllegalFacet extends BaseFacet
{
   @Inject
   private Project project;

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

}
