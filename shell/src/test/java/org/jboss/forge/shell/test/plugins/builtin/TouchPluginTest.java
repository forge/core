package org.jboss.forge.shell.test.plugins.builtin;

import java.io.File;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author Jose Donizetti.
 */
@RunWith(Arquillian.class)
public class TouchPluginTest extends AbstractShellTest
{ 
	
	private Shell shell;
	
	@Before
	public void setup() throws Exception  
	{
		this.shell = getShell();
		File moduleDir = new File("");
		shell.execute("cd " + moduleDir.getAbsolutePath());
	}

	@Test
	public void testAnExistingFile() throws Exception
	{
		File pom = new File(shell.getCurrentResource().getFullyQualifiedName(), "pom.xml");
		long oldLastModified = pom.lastModified();
		
		shell.execute("touch pom.xml");
		
		long newLastModified = pom.lastModified();
		assertTrue("should have changed the file timestamp",newLastModified > oldLastModified);
	}
	
	
	@Test
	public void testANonExistingFile() throws Exception
	{
		shell.execute("touch newFile.txt");
		Resource<?> newFile = shell.getCurrentDirectory().getChild("newFile.txt");
		  
		assertTrue("file should exist",newFile.exists());
		  
		newFile.delete();
	}
}
