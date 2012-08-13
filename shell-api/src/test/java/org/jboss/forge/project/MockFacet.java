/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project;

import org.jboss.forge.project.facets.BaseFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockFacet extends BaseFacet
{
   static final String INSTALLED = "installed";
   private boolean succeed;

   public MockFacet()
   {
      this.succeed = true;
   }

   public MockFacet(boolean succeed)
   {
      this.succeed = succeed;
   }

   @Override
   public boolean install()
   {
      project.setAttribute(INSTALLED, true);
      return succeed;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

}
