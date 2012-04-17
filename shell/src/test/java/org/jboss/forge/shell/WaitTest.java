/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.shell;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class WaitTest extends AbstractShellTest
{
   @Inject
   private Wait wait;
   
   @Inject
   private Shell shell;

   @Test
   public void testWait() throws Exception
   {
       Assert.assertFalse(wait.isWaiting());
       wait.start();
       Assert.assertTrue(wait.isWaiting());
       Thread.sleep(1000);
       wait.stop();
       Assert.assertFalse(wait.isWaiting());
   }

   @Test
   public void testWaitCompletesAfterCommand() throws Exception
   {
       Assert.assertFalse(wait.isWaiting());
       wait.start();
       Assert.assertTrue(wait.isWaiting());
       shell.execute("echo foo");
       Assert.assertFalse(wait.isWaiting());
   }
}
