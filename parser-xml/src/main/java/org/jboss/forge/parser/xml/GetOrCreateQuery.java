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

import java.util.Arrays;
import java.util.List;



/**
 * Starting at the specified {@link Node}, either returns an existing match for the specified {@link Pattern}s, or
 * creates new {@link Node}(s) as appropriate and returns the root of those created.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
enum GetOrCreateQuery implements Query<Node> {

    INSTANCE;

    /**
     * Used in casting
     */
    private static final Pattern[] PATTERN_CAST = new Pattern[] {};

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.descriptor.spi.node.Query#execute(org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.shrinkwrap.descriptor.spi.node.Node,
     *      org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.query.shrinkwrap.descriptor.spi.node.Pattern[])
     */
    @Override
    public Node execute(final Node node, final Pattern... patterns) {
        // Precondition checks
        QueryUtil.validateNodeAndPatterns(node, patterns);

        // Init
        final List<Pattern> patternList = Arrays.asList(patterns);

        // Find or create, starting at the top
        final Node found = this.findOrCreate(node, patternList, patterns);

        // Return
        return found;

    }

    private Node findOrCreate(final Node root, final List<Pattern> patternsToSearch, final Pattern... allPatterns) {

        final Node found = AbsoluteGetSingleQuery.INSTANCE.execute(root, patternsToSearch.toArray(PATTERN_CAST));

        // Not found; we'll have to make it
        if (found == null) {
            // First find the Node we start to match
            if (patternsToSearch.size() > 1) {
                return this.findOrCreate(root, patternsToSearch.subList(0, patternsToSearch.size() - 1), allPatterns);
            }

        }

        // If still not found, nothing at all matched the tree, so go ahead and create the whole thing
        if (found == null) {
            return CreateQuery.INSTANCE.execute(root, allPatterns);
        } else {
            // If this is null, there was no match anywhere in the pattern list
            // Determine which patterns are left to create
            final int offset = patternsToSearch.size();
            final int numPatternsToCreate = allPatterns.length - offset;
            final Pattern[] patternsToCreate = new Pattern[numPatternsToCreate];

            // Only create if there's more to do
            if (patternsToCreate.length > 0) {
                // Copy the patterns we should be creating only
                for (int i = 0; i < numPatternsToCreate; i++) {
                    patternsToCreate[i] = allPatterns[offset + i];
                }

                // Create the new Node and return it
                return CreateQuery.INSTANCE.execute(found, patternsToCreate);
            }
            // Otherwise just return the Node we found (like a "get" operation)
            else {
                return found;
            }
        }

    }

}
