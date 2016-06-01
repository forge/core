/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.util.Set;
import java.util.TreeSet;

import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.Color.Intensity;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.shell.CommandNotFoundListener;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * Suggests similar commands when a specific command is not found
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DidYouMeanCommandNotFoundListener implements CommandNotFoundListener
{
   private static final int LETTERS_NEEDED_TO_BE_REPLACED = 2;

   private final CommandRegistry commandRegistry;

   public DidYouMeanCommandNotFoundListener(CommandRegistry commandRegistry)
   {
      this.commandRegistry = commandRegistry;
   }

   @Override
   public void onCommandNotFound(String line, UIContext context)
   {
      UIOutput output = context.getProvider().getOutput();
      String commandName = line.split(" ")[0];
      // Find similar commands
      Set<String> similarCommands = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      for (String command : commandRegistry.getAllCommandNames())
      {
         if (getLevenshteinDistance(commandName, command) < LETTERS_NEEDED_TO_BE_REPLACED)
         {
            similarCommands.add(command);
         }
      }

      if (!similarCommands.isEmpty())
      {
         output.out().println();
         if (similarCommands.size() == 1)
         {
            output.out().println("Did you mean this?");
         }
         else
         {
            output.out().println("Did you mean one of these?");
         }
         for (String plugin : similarCommands)
         {
            output.out().println(
                     new TerminalString("\t" + plugin,
                              new TerminalColor(Color.DEFAULT, Color.DEFAULT, Intensity.BRIGHT)));
         }
      }

   }

   /**
    * <p>
    * Find the Levenshtein distance between two Strings.
    * </p>
    * 
    * <p>
    * This is the number of changes needed to change one String into another, where each change is a single character
    * modification (deletion, insertion or substitution).
    * </p>
    * 
    * <p>
    * The previous implementation of the Levenshtein distance algorithm was from
    * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>
    * </p>
    * 
    * <p>
    * Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError which can occur when my Java
    * implementation is used with very large strings.<br>
    * This implementation of the Levenshtein distance algorithm is from
    * <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a>
    * </p>
    * 
    * <pre>
    * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
    * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
    * StringUtils.getLevenshteinDistance("","")               = 0
    * StringUtils.getLevenshteinDistance("","a")              = 1
    * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
    * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
    * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
    * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
    * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
    * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
    * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
    * </pre>
    * 
    * @param s the first String, must not be null
    * @param t the second String, must not be null
    * @return result distance
    * @throws IllegalArgumentException if either String input {@code null}
    */
   public static int getLevenshteinDistance(CharSequence s, CharSequence t)
   {
      if (s == null || t == null)
      {
         throw new IllegalArgumentException("Strings must not be null");
      }

      /*
       * The difference between this impl. and the previous is that, rather than creating and retaining a matrix of size
       * s.length() + 1 by t.length() + 1, we maintain two single-dimensional arrays of length s.length() + 1. The
       * first, d, is the 'current working' distance array that maintains the newest distance cost counts as we iterate
       * through the characters of String s. Each time we increment the index of String t we are comparing, d is copied
       * to p, the second int[]. Doing so allows us to retain the previous cost counts as required by the algorithm
       * (taking the minimum of the cost count to the left, up one, and diagonally up and to the left of the current
       * cost count being calculated). (Note that the arrays aren't really copied anymore, just switched...this is
       * clearly much better than cloning an array or doing a System.arraycopy() each time through the outer loop.)
       * 
       * Effectively, the difference between the two implementations is this one does not cause an out of memory
       * condition when calculating the LD over two very large strings.
       */

      int n = s.length(); // length of s
      int m = t.length(); // length of t

      if (n == 0)
      {
         return m;
      }
      else if (m == 0)
      {
         return n;
      }

      if (n > m)
      {
         // swap the input strings to consume less memory
         CharSequence tmp = s;
         s = t;
         t = tmp;
         n = m;
         m = t.length();
      }

      int p[] = new int[n + 1]; // 'previous' cost array, horizontally
      int d[] = new int[n + 1]; // cost array, horizontally
      int _d[]; // placeholder to assist in swapping p and d

      // indexes into strings s and t
      int i; // iterates through s
      int j; // iterates through t

      char t_j; // jth character of t

      int cost; // cost

      for (i = 0; i <= n; i++)
      {
         p[i] = i;
      }

      for (j = 1; j <= m; j++)
      {
         t_j = t.charAt(j - 1);
         d[0] = j;

         for (i = 1; i <= n; i++)
         {
            cost = s.charAt(i - 1) == t_j ? 0 : 1;
            // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
            d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
         }

         // copy current distance counts to 'previous row' distance counts
         _d = p;
         p = d;
         d = _d;
      }

      // our last action in the above loop was to switch d and p, so p now
      // actually has the most recent cost counts
      return p[n];
   }

}
