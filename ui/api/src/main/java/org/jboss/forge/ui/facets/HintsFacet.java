/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.facets;

import org.jboss.forge.facets.Facet;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.input.InputComponent;

/**
 * Hints facet
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface HintsFacet extends Facet<InputComponent<?, ?>>
{
   public abstract InputType getInputType();

   public abstract HintsFacet setInputType(InputType type);

}