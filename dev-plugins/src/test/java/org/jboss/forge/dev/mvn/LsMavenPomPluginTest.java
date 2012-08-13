/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.dev.mvn;

import static org.junit.Assert.assertTrue;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class LsMavenPomPluginTest  extends AbstractShellTest 
{
	
	@Test
    public void testShouldBeAbleToLsPomFile() throws Exception
    {
       Shell shell = getShell();
       shell.execute("cd pom.xml");
       
       shell.execute("ls");
       
       String pom = getOutput();
       assertTrue(pom.contains("[dependencies]"));
       assertTrue(pom.contains("[profiles]"));
       assertTrue(pom.contains("[repositories]"));
    }

}
