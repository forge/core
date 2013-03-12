/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper class for creating queries (collections of {@link Pattern}s) from String query expressions
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
class Patterns
{
   private static final String PATH_SEPARATOR = "(?<!\\\\)[/]";

   private static final String ATTR_PATH_SEPERATOR = "@";

   private static final String ATTR_SEPERATOR = "&";

   private static final String ATTR_VALUE_SEPERATOR = "=";

   private static final Pattern[] ARRAY_CAST = new Pattern[] {};

   /**
    * Creates a query collection of {@link Pattern}s from the specified {@link String}-formed query expression
    * 
    * @param queryExpression
    * @return
    * @throws IllegalArgumentException If the queryExpression is not specified
    */
   public static Pattern[] from(final String queryExpression) throws IllegalArgumentException
   {
      if (queryExpression == null)
      {
         throw new IllegalArgumentException("Query expression must be specified");
      }

      boolean isAbsolute = queryExpression.startsWith("/");
      final Collection<Pattern> patterns = new ArrayList<Pattern>();

      final String[] paths = (isAbsolute ? queryExpression.substring(1) : queryExpression).split(PATH_SEPARATOR);
      for (final String path : paths)
      {
         String nameSegment = path.indexOf(ATTR_PATH_SEPERATOR) != -1 ? path.substring(0,
                  path.indexOf(ATTR_PATH_SEPERATOR)) : path;

         String name = nameSegment.indexOf(ATTR_VALUE_SEPERATOR) != -1 ? nameSegment.substring(0,
                  nameSegment.indexOf(ATTR_VALUE_SEPERATOR)) : nameSegment;
         String text = nameSegment.indexOf(ATTR_VALUE_SEPERATOR) != -1 ? nameSegment.substring(
                  nameSegment.indexOf(ATTR_VALUE_SEPERATOR) + 1).replaceAll("\\\\", "") : null;
         String attribute = path.indexOf(ATTR_PATH_SEPERATOR) != -1 ? path.substring(
                  path.indexOf(ATTR_PATH_SEPERATOR) + ATTR_PATH_SEPERATOR.length(), path.length()) : null;
         String[] attributes = attribute == null ? new String[0] : attribute.split(ATTR_SEPERATOR);

         Pattern pattern = new Pattern(name);
         pattern.text(text);
         for (String attr : attributes)
         {
            String[] nameValue = attr.split(ATTR_VALUE_SEPERATOR);
            if (nameValue.length != 2)
            {
               throw new IllegalArgumentException("Attribute without name or value found: " + attr
                        + " in expression: " + queryExpression);
            }
            pattern.attribute(nameValue[0], nameValue[1]);
         }
         patterns.add(pattern);
      }
      return patterns.toArray(ARRAY_CAST);
   }
}
