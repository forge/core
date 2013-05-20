/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.versions.SingleVersion;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SingleVersionTest
{
   @Test(expected = IllegalArgumentException.class)
   public void testVersionMustNotBeNull()
   {
      new SingleVersion(null);
   }

   @Test
   public void testValidVersion()
   {
      SingleVersion version = new SingleVersion("2.0.0.Final");
      Assert.assertEquals("2.0.0.Final", version.toString());
   }
}
