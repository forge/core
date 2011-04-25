package org.jboss.forge.shell.plugins.builtin;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.ShellEnvCompleter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Mike Brock .
 */
@Alias("set")
@Topic("Shell Environment")
@Help("Sets and lists environment variables")
public class SetPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public SetPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@Option(description = "varname",
                        completer = ShellEnvCompleter.class) final String variable,
                   @Option(description = "value") final String... value)
   {

      if (variable == null)
      {
         listVars();
      }
      else
      {
         shell.setProperty(variable, Echo.tokensToString(value));
      }
   }

   private void listVars()
   {
      for (Map.Entry<String, Object> entry : shell.getProperties().entrySet())
      {
         shell.println(entry.getKey() + "=" + entry.getValue());
      }
   }

}
