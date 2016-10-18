/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandsTest
{
   /**
    * Test method for {@link org.jboss.forge.addon.ui.util.Commands#shellifyOptionValue(java.lang.String)}.
    */
   @Test
   public void testShellifyOptionValue()
   {
      Assert.assertEquals("JAVA_EE_7", Commands.shellifyOptionValue("Java EE 7"));
   }

   @Test
   public void testShellifyCommandName()
   {
      Assert.assertEquals("i-o-jms-activemq", Commands.shellifyCommandName("I/O: JMS (ActiveMQ)"));
      Assert.assertEquals("add-remove-something", Commands.shellifyCommandName("Add/Remove (Something)"));
      Assert.assertEquals("enable-async-i-o-jdk8", Commands.shellifyCommandName("Enable Async I/O (JDK8)"));
   }

}
