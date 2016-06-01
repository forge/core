/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import org.jboss.aesh.cl.renderer.OptionRenderer;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalTextStyle;

/**
 * Possible {@link OptionRenderer} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum OptionRenderers implements OptionRenderer
{
   REQUIRED
   {
      private TerminalTextStyle STYLE = new TerminalTextStyle(CharacterType.BOLD);
      private TerminalColor COLOR = new TerminalColor(42, Color.DEFAULT);

      @Override
      public TerminalColor getColor()
      {
         return COLOR;
      }

      @Override
      public TerminalTextStyle getTextType()
      {
         return STYLE;
      }
   },
   DEPRECATED
   {
      private TerminalTextStyle STYLE = new TerminalTextStyle(CharacterType.CROSSED_OUT);
      private TerminalColor COLOR = new TerminalColor(42, Color.DEFAULT);

      @Override
      public TerminalColor getColor()
      {
         return COLOR;
      }

      @Override
      public TerminalTextStyle getTextType()
      {
         return STYLE;
      }

   }
}
