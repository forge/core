/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.parser.java.resources.JavaFieldResource;
import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.util.Commands;

/**
 * Shell Utilities
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUtil
{
   /**
    * Shellifies a command name
    * 
    * @param name
    * @return
    */
   public static String shellifyCommandName(String name)
   {
      return Commands.shellifyCommandName(name);
   }

   /**
    * Shellifies an option name
    * 
    * @param name
    * @return
    */
   public static String shellifyOptionName(String name)
   {
      return Commands.shellifyOptionName(name);
   }

   /**
    * Shellifies an option name using the provided style
    * 
    * @param name
    * @param style
    * @return
    */
   public static String shellifyOptionNameDashed(String name)
   {
      return Commands.shellifyOptionNameDashed(name);
   }

   /**
    * Applies ANSI colors in a specific resource
    *
    * @param resource
    * @return
    */
   public static String colorizeResource(FileResource<?> resource)
   {
      String name = resource.getName();
      if (resource.isDirectory())
      {
         name = new TerminalString(name, new TerminalColor(Color.BLUE, Color.DEFAULT)).toString();
      }
      else if (resource.isExecutable())
      {
         name = new TerminalString(name, new TerminalColor(Color.GREEN, Color.DEFAULT)).toString();
      }
      return name;
   }

   public static String colorizeJavaMethodResource(JavaMethodResource resource)
   {
      String name = resource.getName();
      String[] splitName = name.split("(?=\\:\\:)"); // split with "::" but preserve delimiter
      return splitName[0] + new TerminalString(splitName[1], new TerminalColor(Color.GREEN, Color.DEFAULT)).toString();
   }

   public static String colorizeJavaFieldResource(JavaFieldResource resource)
   {
      String name = resource.getName();
      String[] splitName = name.split("(?=\\:\\:)"); // split with "::" but preserve delimiter
      return splitName[0] + new TerminalString(splitName[1], new TerminalColor(Color.GREEN, Color.DEFAULT)).toString();
   }

   public static String colorizeLabel(String label)
   {
      return new TerminalString(label, new TerminalColor(Color.RED, Color.DEFAULT)).toString();
   }

   public static TerminalString colorizeResourceTerminal(FileResource<?> resource)
   {
      TerminalString name;
      if (resource.isDirectory())
      {
         name = new TerminalString(resource.getName(), new TerminalColor(Color.BLUE, Color.DEFAULT));
      }
      else if (resource.isExecutable())
      {
         name = new TerminalString(resource.getName(), new TerminalColor(Color.GREEN, Color.DEFAULT));
      }
      else
      {
         name = new TerminalString(resource.getName());
      }
      return name;
   }
}
