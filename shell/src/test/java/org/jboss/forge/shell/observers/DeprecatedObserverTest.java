/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.observers;

import static org.junit.internal.matchers.StringContains.containsString;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class DeprecatedObserverTest extends AbstractShellTest
{

   @Test
   public void testDeprecatedMessage() throws Exception
   {
      getShell().execute("motp deprecated somevalue");
      Assert.assertThat(getOutput(),
               containsString("The command (deprecated) is deprecated and may be removed in future versions"));
   }

}
