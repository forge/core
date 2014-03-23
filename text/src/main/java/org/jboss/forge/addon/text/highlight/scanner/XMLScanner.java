package org.jboss.forge.addon.text.highlight.scanner;


public class XMLScanner extends HTMLScanner {

   public static final Type TYPE = new Type("XML", "\\.(xml|cfc|cfm|tmproj|xaml)$");

   @Override
   public Type getType() {
      return TYPE;
   }
}
