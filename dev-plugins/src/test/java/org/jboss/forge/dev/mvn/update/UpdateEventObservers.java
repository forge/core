package org.jboss.forge.dev.mvn.update;

import javax.enterprise.event.Observes;

import org.jboss.forge.project.dependencies.events.UpdatedDependency;
import org.jboss.forge.project.dependencies.events.UpdatingDependency;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;

public class UpdateEventObservers
{
   public static String VETO_TEXT = "TEST_VETO_DEPENDENCY";
   public static String UPDATED_TEXT = "TEST_UPDATED_DEPENDENCY";
   
   public static void updating(@Observes UpdatingDependency dep, Shell shell)
   {
      if(dep.getTo().getGroupId().startsWith("org.jboss.arquillian.container"))
      {
               dep.veto(VETO_TEXT);
      }
   }

   public static String red(Shell shell, String output) 
   {
      return shell.renderColor(ShellColor.RED, output);
   }
   
   public static void updated(@Observes UpdatedDependency dep, Shell shell)
   {
      if(
            dep.getTo().getGroupId().startsWith("org.jboss.arquillian") && 
            dep.getTo().getArtifactId().startsWith("arquillian-bom"))
      {
         shell.println(UPDATED_TEXT);
      }
   }
}
