package org.jboss.forge.shell.plugins.builtin;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * A simple port of the Unix touch command.
 * 
 * @author Jose Donizetti.
 */
@Alias("touch")
@Topic("File & Resources")
@Help("change file access and modification times")
public class TouchPlugin implements Plugin {

  private final Shell shell;

  @Inject
  public TouchPlugin(final Shell shell) {
    this.shell = shell;
  }

  @DefaultCommand
  public void run(@Option(help = "name of file to be touched", required = true) final String fileName) 
  {

    DirectoryResource dr = (DirectoryResource) shell.getCurrentResource();
    FileResource<?> newResource = (FileResource<?>) dr.getChild(fileName);
    
    if (newResource.exists()) 
    {
      File file = new File(newResource.getFullyQualifiedName());
      Date now = new Date();
      file.setLastModified(now.getTime());
    } 
    else 
    {
      newResource.createNewFile();
    }
  }
}
