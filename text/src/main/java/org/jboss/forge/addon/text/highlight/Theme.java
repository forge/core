package org.jboss.forge.addon.text.highlight;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Theme
{

   private Color defaultColor;
   private Map<TokenType, Color> map;

   public Theme()
   {
      this(null);
   }

   public Theme(Color defaultColor)
   {
      this.defaultColor = defaultColor;
      this.map = new HashMap<TokenType, Color>();
   }

   public Theme set(Color color, TokenType type, TokenType... types)
   {
      this.map.put(type, color);
      if (types != null)
      {
         for (TokenType t : types)
         {
            map.put(t, color);
         }
      }
      return this;
   }

   public Color lookup(TokenType type)
   {
      Color color = map.get(type);
      return color != null ? color : defaultColor;
   }
}
