package org.jboss.forge.addon.text.highlight;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public interface Scanner {

   public enum Type {
      JAVA("\\.(java)$"),
      HTML("\\.(html|xhtml|xml)$"),
      CSS("\\.(css)$"),
      JAVASCRIPT("\\.(js)$"),
      JSON("\\.(json)$"),
      PLAIN(null);

      private Pattern pattern = null;

      Type(String pattern) {
         if(pattern != null) {
            this.pattern = Pattern.compile(pattern);
         }
      }

      public boolean match(String fileName) {
         if(pattern == null) {
            return false;
         }
         return pattern.matcher(fileName).find();
      }

      public static Type byFileName(String fileName) {
         for(Scanner.Type type: Scanner.Type.values()) {
            if(type.match(fileName)) {
               return type;
            }
         }
         return Type.PLAIN;
      }
   }

   void scan(StringScanner source, Encoder encoder, Map<String, Object> options);

   public static class Factory {
      private static Factory factory;

      private Map<String, Class<? extends Scanner>> registry;

      private Factory() {
         this.registry = new HashMap<String, Class<? extends Scanner>>();
      }

      private static Factory instance() {
         if(factory == null) {
            factory = new Factory();
         }
         return factory;
      }

      public static void registrer(String type, Class<? extends Scanner> scanner) {
         instance().registry.put(type, scanner);
      }

      public static Scanner create(String type) {
         Class<? extends Scanner> scanner = instance().registry.get(type);
         if(scanner != null) {
            try {
               return scanner.newInstance();
            } catch(Exception e) {
               throw new RuntimeException("Could not create new instance of " + scanner);
            }
         }
         throw new RuntimeException("No scanner found for type " + type);
      }
   }
}
