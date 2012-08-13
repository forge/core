/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("mockfacet")
public class MockFacet extends BaseFacet
{

   @Override
   public boolean install()
   {
      FileResource<?> child = (FileResource<?>) project.getProjectRoot().getChild("mockFacet.installed");
      return child.createNewFile();
   }

   @Override
   public boolean isInstalled()
   {
      return project.getProjectRoot().getChild("mockFacet.installed").exists();
   }

   @Override
   public boolean uninstall()
   {
      return project.getProjectRoot().getChild("mockFacet.installed").delete();
   }

}
