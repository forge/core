/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Versions;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class VersionsTest
{

   @Test
   public void testAreEqual()
   {
      Assert.assertTrue(Versions.areEqual(new SingleVersion("1"), new SingleVersion("1")));
   }

}
