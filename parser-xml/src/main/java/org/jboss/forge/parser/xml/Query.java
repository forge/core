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
package org.jboss.forge.parser.xml;



/**
 * Contract for something capable of executing a query (collection of {@link Pattern}s) upon a {@link Node} to find a
 * match or matches. May be used for search, creation, etc.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @param <T>
 *            Expected return value from executing a query
 */
interface Query<T> {

    /**
     * Queries the tree starting at the specified {@link Node} for the specified {@link Pattern}s.
     *
     * @param node
     *            The {@link Node} to use as a reference point
     * @param patterns
     *            The {@link Pattern}s to match
     * @return The expressed value or null if not found.
     * @throws IllegalArgumentException
     *             If the {@link Node} is not specified or no {@link Pattern}s are specified
     */
    T execute(Node node, Pattern... patterns) throws IllegalArgumentException;

}
