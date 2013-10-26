/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.util;

import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Shell Utilities
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUtil
{
   /**
    * "Shellifies" a name (that is, makes the name shell-friendly) by replacing spaces with "-" and removing colons
    * 
    * @param name
    * @return
    */
   public static String shellifyName(String name)
   {
      return name.trim().toLowerCase().replaceAll("\\W+", "-").replaceAll("\\:", "");
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
}
