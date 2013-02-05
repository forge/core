/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.facets;

import org.jboss.forge.facets.Facet;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.hints.InputType;

/**
 * Hints facet
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface HintsFacet extends Facet<UIInputComponent<?, ?>>
{
   public abstract InputType getInputType();

   public abstract HintsFacet setInputType(InputType type);

}