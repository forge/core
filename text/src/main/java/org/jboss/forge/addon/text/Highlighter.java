/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text;

import java.io.OutputStream;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;

public class Highlighter
{
   private Imported<Scanner> importedScanners;

   public Highlighter()
   {
      Syntax.builtIns();
   }

   public void byType(String contentType, String content, OutputStream out)
   {
      if (contentType == null)
      {
         throw new IllegalArgumentException("contentType must be specified");
      }
      Imported<Scanner> scanners = resolveScanners();
      for (Scanner scanner : scanners)
      {
         if (scanner.getType().getName().equalsIgnoreCase(contentType))
         {
            try
            {
               execute(scanner, content, out);
               ;
            }
            finally
            {
               scanners.release(scanner);
            }
            return;
         }
      }
      execute(Scanner.Factory.byType(contentType), content, out);
   }

   public void byFileName(String fileName, String content, OutputStream out)
   {
      if (fileName == null)
      {
         throw new IllegalArgumentException("contentType must be specified");
      }
      Imported<Scanner> scanners = resolveScanners();
      for (Scanner scanner : scanners)
      {
         if (scanner.getType().supports(fileName))
         {
            try
            {
               execute(scanner, content, out);
               ;
            }
            finally
            {
               scanners.release(scanner);
            }
            return;
         }
      }
      execute(Scanner.Factory.byFileName(fileName), content, out);
   }

   private void execute(Scanner scanner, String content, OutputStream out)
   {
      if (scanner == null)
      {
         throw new IllegalArgumentException("scanner must be specified");
      }
      if (content == null)
      {
         throw new IllegalArgumentException("content must be specified");
      }
      if (out == null)
      {
         throw new IllegalArgumentException("out must be specified");
      }

      Syntax.Builder.create()
               .encoderType(Encoder.Type.TERMINAL)
               .output(out)
               .scanner(scanner)
               .execute(content);
   }

   private Imported<Scanner> resolveScanners()
   {
      if (importedScanners == null)
      {
         importedScanners = SimpleContainer.getServices(getClass().getClassLoader(), Scanner.class);
      }
      return importedScanners;
   }
}
