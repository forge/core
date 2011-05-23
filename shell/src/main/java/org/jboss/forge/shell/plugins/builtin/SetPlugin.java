package org.jboss.forge.shell.plugins.builtin;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.completer.EnvironmentPropertiesCompleter;
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
   private final ForgeEnvironment forge;
   private final ShellPrintWriter writer;

   @Inject
   public SetPlugin(final ForgeEnvironment forge, ShellPrintWriter writer)
   {
      this.forge = forge;
      this.writer = writer;
   }

   @DefaultCommand
   public void run(@Option(description = "varname",
                        completer = EnvironmentPropertiesCompleter.class) final String variable,
                   @Option(description = "value") final String... value)
   {

      if (variable == null)
      {
         listVars();
      }
      else
      {
         forge.setProperty(variable, Echo.tokensToString(value));
      }
   }

   private void listVars()
   {
      for (Map.Entry<String, Object> entry : forge.getProperties().entrySet())
      {
         writer.println(entry.getKey() + "=" + entry.getValue());
      }
   }

}
