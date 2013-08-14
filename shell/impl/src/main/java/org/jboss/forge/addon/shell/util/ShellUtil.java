/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.util;

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

}
