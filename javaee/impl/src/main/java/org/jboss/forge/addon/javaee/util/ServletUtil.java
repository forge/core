/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.util;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServletUtil
{
   /**
    * Convert a Servlet Mapping to a compiled {@link Pattern}
    */
   public static Pattern mappingToRegex(String mapping)
   {
      return Pattern.compile(mapping.replaceAll("\\.", "\\.")
               .replaceAll("^\\*(.*)", "^(.*)$1\\$")
               .replaceAll("(.*)\\*$", "^$1(.*)\\$"));
   }
}