package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Donizetti.
 */
@RunWith(Arquillian.class)
public class TouchPluginTest extends AbstractShellTest
{ 
	
	@Test
	public void testAnExistingFile() throws Exception
	{
		Shell shell = getShell();
		File pom = new File(shell.getCurrentResource().getFullyQualifiedName(), "pom.xml");
		long oldLastModified = pom.lastModified();
		
		shell.execute("touch pom.xml");
		
		long newLastModified = pom.lastModified();
		assertTrue("should have changed the file timestamp",newLastModified > oldLastModified);
	}
	
	
	@Test
	public void testANonExistingFile() throws Exception
	{
		Shell shell = getShell();
		shell.execute("touch newFile.txt");
		Resource<?> newFile = shell.getCurrentDirectory().getChild("newFile.txt");
		  
		assertTrue("file should exist",newFile.exists());
		  
		newFile.delete();
	}
}
