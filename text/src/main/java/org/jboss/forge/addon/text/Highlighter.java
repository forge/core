package org.jboss.forge.addon.text;

import java.io.OutputStream;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.furnace.container.simple.Service;

public class Highlighter implements Service {

   public void byType(String contentType, String content, OutputStream out) {
      if(contentType == null) {
         throw new IllegalArgumentException("contentType must be specified");
      }
      if(content == null) {
         throw new IllegalArgumentException("content must be specified");
      }
      if(out == null) {
         throw new IllegalArgumentException("out must be specified");
      }

      Syntax.Builder.create()
         .encoderType(Encoder.Type.TERMINAL)
         .output(out)
         .scannerType(contentType.toUpperCase())
         .execute(content);
   }

   public void byFileName(String fileName, String content, OutputStream out) {
      byType(Scanner.Type.byFileName(fileName).name(), content, out);
   }
}
