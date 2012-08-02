package org.jboss.forge.shell.plugins.builtin;

import java.io.File;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
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

  @DefaultCommand
  public void run(@Option(help = "name of file to be touched", required = true) final Resource<?>  resource)
  {
	FileResource<?> fileResource = (FileResource<?>) resource;
    if (fileResource.exists())
    {
      File file = fileResource.getUnderlyingResourceObject();
      file.setLastModified(System.currentTimeMillis());
    }
    else
    {
      fileResource.createNewFile();
    }
  }
}
