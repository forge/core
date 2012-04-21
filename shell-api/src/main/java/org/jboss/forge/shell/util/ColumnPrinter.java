package org.jboss.forge.shell.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;

/**
 * Displays output in Shell in columns
 *
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 *
 */
public class ColumnPrinter
{
   protected OutputAttributes calculateOutputAttributes(final List<String> rawList, final Shell shell,
            final OutputAttributes in)
   {
      if (in == null)
      {
         return calculateOutputAttributes(rawList, shell);
      }

      OutputAttributes newAttr = calculateOutputAttributes(rawList, shell);

      return new OutputAttributes(in.columnSize > newAttr.columnSize ? in.columnSize : newAttr.columnSize,
               in.columns < newAttr.columns ? in.columns : newAttr.columns);
   }

   protected OutputAttributes calculateOutputAttributes(final List<String> rawList, final Shell shell)
   {
      int width = shell.getWidth();
      int maxLength = 0;

      for (String s : rawList)
      {
         if (s.length() > maxLength)
         {
            maxLength = s.length();
         }
      }
      int cols = width / (maxLength + 4);
      int colSize;

      if (cols == 0)
      {
         colSize = width;
         cols = 1;

      }
      else
      {
         colSize = width / cols;
      }

      return new OutputAttributes(colSize, cols);
   }

   public void printOutColumns(final List<String> rawList, final ShellPrintWriter out, final Shell shell,
            final boolean sort)
   {
      printOutColumns(rawList, ShellColor.NONE, out, calculateOutputAttributes(rawList, shell), null, sort);
   }

   protected void printOutColumns(final List<String> rawList, final ShellPrintWriter out, final Shell shell,
            final FormatCallback callback, final boolean sort)
   {
      printOutColumns(rawList, ShellColor.NONE, out, calculateOutputAttributes(rawList, shell), callback, sort);
   }

   protected void printOutColumns(final List<String> rawList, final ShellColor color,
            final ShellPrintWriter printWriter,
            final OutputAttributes attributes, final FormatCallback callback,
            final boolean sort)
   {
      if (sort)
      {
         Collections.sort(rawList);
      }

      int cols = attributes.columns;
      int colSize = attributes.columnSize;

      int i = 0;
      for (String s : rawList)
      {
         String out = callback != null ? callback.format(0, s) : s;
         if (color == ShellColor.NONE)
         {
            printWriter.print(out);
         }
         else
         {
            printWriter.print(color, out);
         }

         if (++i != cols)
         {
            printWriter.print(pad(colSize - s.length()));
         }

         if (i == cols)
         {
            printWriter.println();
            i = 0;
         }
      }
      if ((i != 0) && (i != cols))
      {
         printWriter.println();
      }
   }

   public void printOutTables(final List<String> list, final int cols, final Shell shell)
   {
      printOutTables(list, new boolean[cols], shell, null);
   }

   public void printOutTables(final List<String> list, final boolean[] columns, final Shell shell)
   {
      printOutTables(list, columns, shell, null);
   }

   protected void printOutTables(final List<String> list, final boolean[] columns, final ShellPrintWriter shell,
            final FormatCallback callback)
   {
      int cols = columns.length;
      int[] colSizes = new int[columns.length];

      Iterator<String> iter = list.iterator();

      String el;
      while (iter.hasNext())
      {
         for (int i = 0; i < cols; i++)
         {
            if (colSizes[i] < (el = iter.next()).length())
            {
               colSizes[i] = el.length();
            }
         }
      }

      iter = list.iterator();

      while (iter.hasNext())
      {
         for (int i = 0; i < cols; i++)
         {
            el = iter.next();
            if (columns[i])
            {
               shell.print(pad(colSizes[i] - el.length()));
               if (callback != null)
               {
                  shell.print(callback.format(i, el));
               }
               else
               {
                  shell.print(el);
               }

               if (iter.hasNext())
               {
                  shell.print(" ");
               }
            }
            else
            {
               if (callback != null)
               {
                  shell.print(callback.format(i, el));
               }
               else
               {
                  shell.print(el);
               }

               if (iter.hasNext())
               {
                  shell.print(pad(colSizes[i] - el.length()));
                  shell.print(" ");
               }
            }
         }
         shell.println();
      }
   }

   protected String pad(final int amount)
   {
      char[] padding = new char[amount];
      for (int i = 0; i < amount; i++)
      {
         padding[i] = ' ';
      }
      return new String(padding);
   }

   protected static interface FormatCallback
   {
      public String format(int column, String value);
   }

   protected static class OutputAttributes
   {
      public OutputAttributes(final int columnSize, final int columns)
      {
         this.columnSize = columnSize;
         this.columns = columns;
      }

      private final int columnSize;
      private final int columns;
   }

}
