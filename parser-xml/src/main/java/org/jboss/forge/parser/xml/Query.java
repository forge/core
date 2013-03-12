/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

/**
 * Contract for something capable of executing a query (collection of {@link Pattern}s) upon a {@link Node} to find a
 * match or matches. May be used for search, creation, etc.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @param <T> Expected return value from executing a query
 */
interface Query<T>
{

   /**
    * Queries the tree starting at the specified {@link Node} for the specified {@link Pattern}s.
    * 
    * @param node The {@link Node} to use as a reference point
    * @param patterns The {@link Pattern}s to match
    * @return The expressed value or null if not found.
    * @throws IllegalArgumentException If the {@link Node} is not specified or no {@link Pattern}s are specified
    */
   T execute(Node node, Pattern... patterns) throws IllegalArgumentException;

}
