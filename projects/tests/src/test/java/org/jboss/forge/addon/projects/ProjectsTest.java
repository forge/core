/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectsTest
{

   /**
    * Test method for {@link org.jboss.forge.addon.projects.Projects#enableCache()}.
    */
   @Test
   public void testEnableCache()
   {
      Projects.enableCache();
      Assert.assertFalse(Projects.isCacheDisabled());
   }

   /**
    * Test method for {@link org.jboss.forge.addon.projects.Projects#disableCache()}.
    */
   @Test
   public void testDisableCache()
   {
      Projects.disableCache();
      Assert.assertTrue(Projects.isCacheDisabled());
   }
}
