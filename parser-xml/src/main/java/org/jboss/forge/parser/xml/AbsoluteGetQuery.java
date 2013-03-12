/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * Form of {@link GetQuery} for retrieving nodes matching absolute patterns like '/root/node1/node2'. Sequence of
 * patterns must much a path from the root, where i-th pattern matches i-th element on the path from the root.
 *
 * If no matches are found, <code>null</code> is returned.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
enum AbsoluteGetQuery implements Query<List<Node>> {

    INSTANCE;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.descriptor.spi.node.Query#execute(org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.shrinkwrap.descriptor.spi.node.Node,
     *      org.jboss.forge.parser.xml.org.jboss.forge.parser.xml.query.shrinkwrap.descriptor.spi.node.Pattern[])
     */
    @Override
    public List<Node> execute(final Node node, final Pattern... patterns) throws IllegalArgumentException {
        // Precondition checks
        QueryUtil.validateNodeAndPatterns(node, patterns);

        // Delegate to recursive handler, starting at the top
        return findMatch(node, Arrays.asList(patterns));
    }

    protected List<Node> findMatch(Node start, List<Pattern> patterns) {
        // Get the next pattern in sequence
        final Pattern pattern = patterns.get(0);

        if (!pattern.matches(start)) {
            return Collections.emptyList();
        }

        // Hold the matched Nodes
        final List<Node> matchedNodes = new ArrayList<Node>();

        if (patterns.size() == 1) {
            matchedNodes.add(start);
            return matchedNodes;
        }

        for (final Node child : start.getChildren()) {
            // Only use patterns that haven't already matched
            final List<Pattern> remainingPatterns = patterns.subList(1, patterns.size());

            // Recursion point
            matchedNodes.addAll(findMatch(child, remainingPatterns));
        }

        return matchedNodes;
    }

}
