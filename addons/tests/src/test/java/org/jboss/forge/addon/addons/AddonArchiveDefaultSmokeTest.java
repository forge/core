/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons;

import static org.hamcrest.CoreMatchers.notNullValue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.furnace.Furnace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Smoke test for tests without a {@link Deployment} method
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class AddonArchiveDefaultSmokeTest
{
   @Inject
   Furnace furnace;

   @Test
   public void test()
   {
      Assert.assertThat(furnace, notNullValue());
   }

}
