package org.jboss.forge.addon.shell.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.util.Strings;

/**
 * 
 * @author <a href="mailto:md.benhassine@gmail.com">Mahmoud Ben Hassine</a>
 */
public class DateCommand
{
   @Command(value = "date", help = "print current date", enabled = NonGUIEnabledPredicate.class)
   public Result execute(
            @Option(value = "pattern", description = "The date pattern") final String pattern,
            final UIOutput output)
   {
      SimpleDateFormat dateFormat;
      if (!Strings.isNullOrEmpty(pattern))
      {
         try
         {
            dateFormat = new SimpleDateFormat(pattern);
         }
         catch (IllegalArgumentException iae)
         {
            return Results.fail("Illegal date pattern: " + pattern);
         }
      }
      else
      {
         dateFormat = new SimpleDateFormat();
      }

      String date = dateFormat.format(new Date());
      output.out().println(date);
      return Results.success();

   }

}
