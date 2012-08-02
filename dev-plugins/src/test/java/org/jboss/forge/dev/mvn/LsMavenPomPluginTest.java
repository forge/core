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
