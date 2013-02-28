/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.context;


/**
 * Represents the objects with on which the {@link UIContext} is currently focused. This may be the current working
 * directory, highlighted files, text, or other focusable items.
 *
 * This object and the iterator provided by this object are immutable.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @param <SELECTIONTYPE> The selection type, must be common between all selections (if multiple).
 */
public interface UISelection<SELECTIONTYPE> extends Iterable<SELECTIONTYPE>
{
   /**
    * Provides the first selected element as a {@code SELECTIONTYPE}.
    *
    * This is a convenience method to avoid iteration.
    *
    * @return the first element in this selection
    */
   public SELECTIONTYPE get();

   /**
    * Counts the elements in this selection.
    *
    * @return the number of selected elements
    */
   public int size();

}
