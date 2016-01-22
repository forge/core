/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.context;

import java.util.Optional;

/**
 * Provides location information about an element in the source file
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIRegion<SELECTIONTYPE>
{
   /**
    * Returns the character index into the original source file indicating where the source fragment corresponding to
    * this element begins.
    * 
    * @return the 0-based character index, or -1 if no source position information is recorded for this element
    */
   int getStartPosition();

   /**
    * Returns the character index into the original source file indicating where the source fragment corresponding to
    * this element ends.
    *
    * @return the 0-based character index, or -1 if no source position information is recorded for this element
    */
   int getEndPosition();

   /**
    * Returns number of the line containing the offset of the selected text. If the underlying text has been changed
    * between the creation of this selection object and the call of this method, the value returned might differ from
    * what it would have been at the point of creation.
    *
    * @return the start line of this selection or -1 if there is no valid line information
    */
   int getStartLine();

   /**
    * Returns the number of the line containing the last character of the selected text. If the underlying text has been
    * changed between the creation of this selection object and the call of this method, the value returned might differ
    * from what it would have been at the point of creation.
    *
    * @return the end line of this selection or -1 if there is no valid line information
    */
   int getEndLine();

   /**
    * Returns the selected text. If the underlying text has been changed between the creation of this selection object
    * and the call of this method, the value returned might differ from what it would have been at the point of
    * creation.
    *
    * @return an {@link Optional} containing the selected text. May be empty if there is no valid text information
    */
   Optional<String> getText();

   /**
    * Returns the selected resource that this {@link UIRegion} belongs to.
    * 
    * @return the resource that this {@link UIRegion} belongs to.
    */
   SELECTIONTYPE getResource();
}
