/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.parser.xml.util;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.forge.parser.xml.query.Pattern;

/**
 * Helper class for creating queries (collections of {@link Pattern}s) from String query expressions
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class Patterns
{
   private static final String PATH_SEPARATOR = "/";

   private static final String ATTR_PATH_SEPERATOR = "@";

   private static final String ATTR_SEPERATOR = "&";

   private static final String ATTR_VALUE_SEPERATOR = "=";

   private static final Pattern[] ARRAY_CAST = new Pattern[]
   {};

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
         throw new IllegalArgumentException("query expression must be specified");
      }

      boolean isAbsolute = queryExpression.startsWith(PATH_SEPARATOR);
      final Collection<Pattern> patterns = new ArrayList<Pattern>();

      final String[] paths = (isAbsolute ? queryExpression.substring(1) : queryExpression).split(PATH_SEPARATOR);
      for (final String path : paths)
      {
         String nameSegment = path.indexOf(ATTR_PATH_SEPERATOR) != -1 ? path.substring(0,
                  path.indexOf(ATTR_PATH_SEPERATOR)) : path;

         String name = nameSegment.indexOf(ATTR_VALUE_SEPERATOR) != -1 ? nameSegment.substring(0,
                  nameSegment.indexOf(ATTR_VALUE_SEPERATOR)) : nameSegment;
         String text = nameSegment.indexOf(ATTR_VALUE_SEPERATOR) != -1 ? nameSegment.substring(nameSegment
                  .indexOf(ATTR_VALUE_SEPERATOR) + 1) : null;
         String attribute = path.indexOf(ATTR_PATH_SEPERATOR) != -1 ? path.substring(path.indexOf(ATTR_PATH_SEPERATOR)
                  + ATTR_PATH_SEPERATOR.length(), path.length()) : null;
         String[] attributes = attribute == null ? new String[0] : attribute.split(ATTR_SEPERATOR);

         Pattern pattern = new Pattern(name);
         pattern.text(text);
         for (String attr : attributes)
         {
            String[] nameValue = attr.split(ATTR_VALUE_SEPERATOR);
            if (nameValue.length != 2)
            {
               throw new IllegalArgumentException("Attribute without name or value found: " + attr + " in expression: "
                        + queryExpression);
            }
            pattern.attribute(nameValue[0], nameValue[1]);
         }
         patterns.add(pattern);
      }
      return patterns.toArray(ARRAY_CAST);
   }
}
