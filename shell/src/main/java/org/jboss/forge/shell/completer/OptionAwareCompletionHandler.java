/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.console.jline.console.ConsoleReader;
import org.jboss.forge.shell.console.jline.console.CursorBuffer;
import org.jboss.forge.shell.console.jline.console.completer.CandidateListCompletionHandler;
import org.jboss.forge.shell.console.jline.console.completer.CompletionHandler;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * 
 */
public class OptionAwareCompletionHandler implements CompletionHandler
{
   // TODO: handle quotes and escaped quotes && enable automatic escaping of whitespace
   private final CompletedCommandHolder commandHolder;
   private final Shell shell;

   public OptionAwareCompletionHandler(final CompletedCommandHolder commandHolder, final Shell shell)
   {
      this.commandHolder = commandHolder;
      this.shell = shell;
   }

   @Override
   public boolean complete(final ConsoleReader reader, final List<CharSequence> candidates, final int pos) throws
            IOException
   {
      CursorBuffer buf = reader.getCursorBuffer();

      PluginCommandCompleterState state = commandHolder.getState();
      if (state != null)
      {
         if (((candidates.size() == 1) && "".equals(candidates.get(0)))
                  || (state.isDuplicateBuffer() && state.isFinalTokenComplete()))
         {
            if (commandHolder.getState().getOption() != null)
            {
               OptionMetadata option = commandHolder.getState().getOption();
               reader.println();
               reader.println(option.getOptionDescriptor());
               if (candidates.size() == 1)
               {
                  reader.println();
                  reader.drawLine();
                  return true;
               }
            }
         }
      }

      // if there is only one completion, then fill in the buffer
      if (candidates.size() == 1)
      {
         String value = candidates.get(0).toString();
         // escape the spaces, except if it's the last character
         if (!Strings.isNullOrEmpty(value))
            value = value.substring(0, value.length() - 1).replace(" ", "\\ ")
                     + value.substring(value.length() - 1);

         // fail if the only candidate is the same as the current buffer
         if (value.equals(buf.toString()))
         {
            return false;
         }

         setBuffer(reader, value, pos);

         return true;
      }
      else if (candidates.size() > 1)
      {
         // escape all the spaces, even the last character
         CharSequence value = getUnambiguousCompletions(candidates).replace(" ", "\\ ");
         setBuffer(reader, value, pos);
      }

      printCandidates(reader, candidates);

      // redraw the current console buffer
      reader.drawLine();
      reader.flush();

      return true;
   }

   public static void setBuffer(final ConsoleReader reader, final CharSequence value, final int offset) throws
            IOException
   {
      while ((reader.getCursorBuffer().cursor > offset) && reader.backspace())
      {
         // empty
      }

      reader.putString(value);
      reader.setCursorPosition(offset + value.length());
   }

   /**
    * Print out the candidates. If the size of the candidates is greater than the
    * {@link org.jboss.forge.shell.console.ConsoleReader#getAutoprintThreshold}, they prompt with a warning.
    * 
    * @param candidates the list of candidates to print
    */
   public void printCandidates(final ConsoleReader reader, Collection<CharSequence> candidates) throws
            IOException
   {
      Set<CharSequence> distinct = new HashSet<CharSequence>(candidates);

      if (distinct.size() > reader.getAutoprintThreshold())
      {
         // noinspection StringConcatenation
         reader.print(Messages.DISPLAY_CANDIDATES.format(candidates.size()));
         reader.flush();

         int c;

         String noOpt = Messages.DISPLAY_CANDIDATES_NO.format();
         String yesOpt = Messages.DISPLAY_CANDIDATES_YES.format();
         char[] allowed = { yesOpt.charAt(0), noOpt.charAt(0) };

         while ((c = reader.readCharacter(allowed)) != -1)
         {
            String tmp = new String(new char[] { (char) c });

            if (noOpt.startsWith(tmp))
            {
               reader.println();
               reader.flush();
               return;
            }
            else if (yesOpt.startsWith(tmp))
            {
               break;
            }
            else
            {
               reader.beep();
               reader.flush();
            }
         }
      }

      // copy the values and make them distinct, without otherwise affecting the ordering. Only do it if the sizes
      // differ.
      if (distinct.size() != candidates.size())
      {
         Collection<CharSequence> copy = new ArrayList<CharSequence>();

         for (CharSequence next : candidates)
         {
            if (!copy.contains(next))
            {
               copy.add(next);
            }
         }

         candidates = copy;
      }

      reader.println();
      reader.flush();

      Collection<CharSequence> colorizedCandidates = new ArrayList<CharSequence>();
      for (CharSequence seq : candidates)
      {
         boolean processed = false;
         if (commandHolder.getState() != null)
         {
            CommandMetadata command = commandHolder.getState().getCommand();
            if ((command != null) && seq.toString().startsWith("--"))
            {
               String str = seq.toString().trim();
               if (str.startsWith("--"))
               {
                  str = str.substring(2);
               }

               if (command.hasOption(str) && command.getNamedOption(str).isRequired())
               {
                  seq = shell.renderColor(ShellColor.BLUE, seq.toString());
                  colorizedCandidates.add(seq);
                  processed = true;
               }
            }
         }
         if (!processed)
         {
            colorizedCandidates.add(seq);
         }
      }

      reader.printColumns(colorizedCandidates);
      reader.flush();
   }

   /**
    * Returns a root that matches all the {@link String} elements of the specified {@link List}, or null if there are no
    * commonalities. For example, if the list contains <i>foobar</i>, <i>foobaz</i>, <i>foobuz</i>, the method will
    * return <i>foob</i>.
    */
   private String getUnambiguousCompletions(final List<CharSequence> candidates)
   {
      if ((candidates == null) || candidates.isEmpty())
      {
         return null;
      }

      // convert to an array for speed
      String[] strings = candidates.toArray(new String[candidates.size()]);

      String first = strings[0];
      StringBuilder candidate = new StringBuilder();

      for (int i = 0; i < first.length(); i++)
      {
         if (startsWith(first.substring(0, i + 1), strings))
         {
            candidate.append(first.charAt(i));
         }
         else
         {
            break;
         }
      }

      return candidate.toString();
   }

   /**
    * @return true is all the elements of <i>candidates</i> start with <i>starts</i>
    */
   private boolean startsWith(final String starts, final String[] candidates)
   {
      for (String candidate : candidates)
      {
         if (!candidate.startsWith(starts))
         {
            return false;
         }
      }

      return true;
   }

   private static enum Messages
   {
      DISPLAY_CANDIDATES,
      DISPLAY_CANDIDATES_YES,
      DISPLAY_CANDIDATES_NO;

      public String format(final Object... args)
      {
         ResourceBundle bundle = ResourceBundle.getBundle(CandidateListCompletionHandler.class.getName());
         return String.format(bundle.getString(name()), args);
      }
   }
}
