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