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
package org.jboss.forge.parser.xml.query;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.util.QueryUtil;

/**
 * Starting at the specified {@link Node}, either returns an existing match for the specified {@link Pattern}s, or
 * creates new {@link Node}(s) as appropriate and returns the root of those created.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public enum GetOrCreateQuery implements Query<Node>
{

   /**
    * Instance
    */
   INSTANCE;

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(GetOrCreateQuery.class.getName());

   /**
    * Used in casting
    */
   private static final Pattern[] PATTERN_CAST = new Pattern[]
   {};

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.descriptor.spi.node.query.Query#execute(org.jboss.shrinkwrap.descriptor.spi.node.Node,
    *      org.jboss.shrinkwrap.descriptor.spi.node.query.Pattern[])
    */
   @Override
   public Node execute(final Node node, final Pattern... patterns)
   {
      // Precondition checks
      QueryUtil.validateNodeAndPatterns(node, patterns);

      // Init
      final List<Pattern> patternList = Arrays.asList(patterns);
      if (log.isLoggable(Level.FINEST))
      {
         log.finest("Looking to create: " + patternList + " on " + node.toString(true));
      }

      // Find or create, starting at the top
      final Node found = this.findOrCreate(node, patternList, patterns);

      // Return
      return found;

   }

   private Node findOrCreate(final Node root, final List<Pattern> patternsToSearch,
            final Pattern... allPatterns)
   {

      final Node found = GetSingleQuery.INSTANCE.execute(root, patternsToSearch.toArray(PATTERN_CAST));

      // Not found; we'll have to make it
      if (found == null)
      {
         // First find the Node we start to match
         if (patternsToSearch.size() > 1)
         {
            return this.findOrCreate(root, patternsToSearch.subList(0, patternsToSearch.size() - 1),
                     allPatterns);
         }

      }

      // If still not found, nothing at all matched the tree, so go ahead and create the whole thing
      if (found == null)
      {
         if (log.isLoggable(Level.FINEST))
         {
            log.finest("Still not found, root: " + root);
         }
         return CreateQuery.INSTANCE.execute(root, allPatterns);
      }
      else
      {
         // If this is null, there was no match anywhere in the pattern list
         if (log.isLoggable(Level.FINEST))
         {
            log.finest("Found " + found + " matching " + patternsToSearch);
         }

         // Determine which patterns are left to create
         final int offset = patternsToSearch.size();
         final int numPatternsToCreate = allPatterns.length - offset;
         final Pattern[] patternsToCreate = new Pattern[numPatternsToCreate];

         // Only create if there's more to do
         if (patternsToCreate.length > 0)
         {
            // Copy the patterns we should be creating only
            for (int i = 0; i < numPatternsToCreate; i++)
            {
               patternsToCreate[i] = allPatterns[offset + i];
            }
            if (log.isLoggable(Level.FINEST))
            {
               log.finest("Attempting to create " + Arrays.asList(patternsToCreate) + " on " + found);
            }

            // Create the new Node and return it
            final Node newNode = CreateQuery.INSTANCE.execute(found, patternsToCreate);
            return newNode;
         }
         // Otherwise just return the Node we found (like a "get" operation)
         else
         {
            return found;
         }
      }

   }

}
