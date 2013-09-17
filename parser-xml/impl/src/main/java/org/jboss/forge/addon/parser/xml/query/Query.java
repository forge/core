/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml.query;

import org.jboss.forge.addon.parser.xml.Node;
import org.jboss.forge.addon.parser.xml.NodeImpl;
import org.jboss.forge.addon.parser.xml.Pattern;


/**
 * Contract for something capable of executing a query (collection of {@link PatternImpl}s) upon a {@link NodeImpl} to find a
 * match or matches. May be used for search, creation, etc.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @param <T> Expected return value from executing a query
 */
public interface Query<T>
{
   /**
    * Queries the tree starting at the specified {@link NodeImpl} for the specified {@link PatternImpl}s.
    * 
    * @param node The {@link NodeImpl} to use as a reference point
    * @param patterns The {@link PatternImpl}s to match
    * @return The expressed value or null if not found.
    * @throws IllegalArgumentException If the {@link NodeImpl} is not specified or no {@link PatternImpl}s are specified
    */
   T execute(Node node, Pattern... patterns) throws IllegalArgumentException;
}
