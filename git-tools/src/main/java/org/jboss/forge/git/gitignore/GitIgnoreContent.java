/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.git.gitignore;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.RequiresResource;

/**
 * @author Dan Allen
 */
@Alias("gitignore-edit")
@Help("Manage the contents of .gitignore files")
@RequiresProject
@RequiresResource(GitIgnoreResource.class)
public class GitIgnoreContent implements Plugin
{

   @Inject
   @Current
   private GitIgnoreResource gitIgnore;

   @Inject
   private Project project;

   @Inject
   private Shell shell;

   @Command(help = "List the ignore patterns")
   public void list(PipeOut out)
   {
      for (String pattern : gitIgnore.getPatterns())
      {
         out.println(pattern);
      }
   }

   @Command(help = "Add ignore pattern")
   public void add(@Option(description = "pattern", required = true) String pattern, PipeOut out)
   {
      gitIgnore.addPattern(pattern);
      out.println("Pattern added to the .gitignore in the current directory");
   }

   @Command(help = "Remove ignore pattern")
   public void remove(
            @Option(description = "pattern", required = true, completer = GitIgnorePatternCompleter.class) String pattern,
            PipeOut out)
   {
      gitIgnore.removePattern(pattern);
      out.println("Pattern removed from the .gitignore in the current directory");
   }

}
