/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ScriptOperationsTest
{
   protected ScriptOperations scriptOperations;

   @Before
   public void setUp()
   {
      scriptOperations = SimpleContainer.getServices(getClass().getClassLoader(), ScriptOperations.class).get();
   }

   @Test
   public void testLookupScriptOperationsThroughContainer()
   {
      Assert.assertThat(scriptOperations, notNullValue());
   }

   @Test
   public void testScriptOperationsEvaluateShouldSucceed() throws Exception
   {
      Result result = scriptOperations.evaluate(OperatingSystemUtils.getUserHomeDir(), "pwd", 500, null, null);
      Assert.assertThat(result, not(instanceOf(Failed.class)));
   }
}
