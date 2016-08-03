/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.context;

import java.util.Optional;

/**
 * Builder implementation of {@link UIRegion}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class UIRegionBuilder<SELECTIONTYPE> implements UIRegion<SELECTIONTYPE>
{

   private final SELECTIONTYPE resource;

   private int startPosition;
   private int endPosition;
   private int startLine;
   private int endLine;
   private String text;

   private UIRegionBuilder(SELECTIONTYPE resource)
   {
      this.resource = resource;
   }

   public static <SELECTIONTYPE> UIRegionBuilder<SELECTIONTYPE> create(SELECTIONTYPE resource)
   {
      return new UIRegionBuilder<>(resource);
   }

   public UIRegionBuilder<SELECTIONTYPE> startPosition(int startPosition)
   {
      this.startPosition = startPosition;
      return this;
   }

   public UIRegionBuilder<SELECTIONTYPE> endPosition(int endPosition)
   {
      this.endPosition = endPosition;
      return this;
   }

   public UIRegionBuilder<SELECTIONTYPE> startLine(int startLine)
   {
      this.startLine = startLine;
      return this;
   }

   public UIRegionBuilder<SELECTIONTYPE> endLine(int endLine)
   {
      this.endLine = endLine;
      return this;
   }

   public UIRegionBuilder<SELECTIONTYPE> text(String text)
   {
      this.text = text;
      return this;
   }

   @Override
   public int getStartPosition()
   {
      return startPosition;
   }

   @Override
   public int getEndPosition()
   {
      return endPosition;
   }

   @Override
   public int getStartLine()
   {
      return startLine;
   }

   @Override
   public int getEndLine()
   {
      return endLine;
   }

   @Override
   public Optional<String> getText()
   {
      return Optional.ofNullable(text);
   }

   @Override
   public SELECTIONTYPE getResource()
   {
      return resource;
   }

}
