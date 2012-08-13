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
 * @author <a href="mailto:sachsedaniel@gmail.com">Daniel 'Wombat' Sachse</a>
 * 
 */
@Alias("mockfacet2")
public class MockFacet2 extends BaseFacet
{

   @Override
   public boolean install()
   {
      FileResource<?> child = (FileResource<?>) project.getProjectRoot().getChild("mockFacet2.installed");

      return child.createNewFile();
   }

   @Override
   public boolean isInstalled()
   {
      return project.getProjectRoot().getChild("mockFacet2.installed").exists();
   }

   @Override
   public boolean uninstall()
   {
      return project.getProjectRoot().getChild("mockFacet2.installed").delete();
   }
}