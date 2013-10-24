/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import org.jboss.aesh.cl.renderer.OptionRenderer;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;

/**
 * Possible {@link OptionRenderer} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum OptionRenderers implements OptionRenderer
{
   REQUIRED
   {
      @Override
      public Color getBackgroundColor()
      {
         return Color.DEFAULT_BG;
      }

      @Override
      public Color getTextColor()
      {
         return Color.DEFAULT_TEXT;
      }

      @Override
      public CharacterType getTextType()
      {
         return CharacterType.BOLD;
      }
   }
}
