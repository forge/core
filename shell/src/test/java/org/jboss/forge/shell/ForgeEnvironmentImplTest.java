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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeEnvironmentImplTest extends AbstractShellTest
{
   @Inject
   private ForgeEnvironment environment;

   @Test
   public void testCreateProject() throws Exception
   {
      Shell shell = getShell();

      assertTrue(environment.isOnline());
      shell.execute("set OFFLINE true");
      assertFalse(environment.isOnline());
   }

   @Test
   public void testGetRuntimeVersion() throws Exception
   {
      String version = getClass().getPackage().getImplementationVersion();
      Assert.assertEquals(version, environment.getRuntimeVersion());
   }
}
