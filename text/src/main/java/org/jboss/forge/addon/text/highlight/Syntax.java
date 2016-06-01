/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.jboss.forge.addon.text.highlight.encoder.DebugEncoder;
import org.jboss.forge.addon.text.highlight.encoder.TerminalEncoder;
import org.jboss.forge.addon.text.highlight.scanner.CSSScanner;
import org.jboss.forge.addon.text.highlight.scanner.GroovyScanner;
import org.jboss.forge.addon.text.highlight.scanner.HTMLScanner;
import org.jboss.forge.addon.text.highlight.scanner.JSONScanner;
import org.jboss.forge.addon.text.highlight.scanner.JavaScanner;
import org.jboss.forge.addon.text.highlight.scanner.JavaScriptScanner;
import org.jboss.forge.addon.text.highlight.scanner.PlainScanner;
import org.jboss.forge.addon.text.highlight.scanner.PropertiesScanner;
import org.jboss.forge.addon.text.highlight.scanner.SQLScanner;
import org.jboss.forge.addon.text.highlight.scanner.XMLScanner;
import org.jboss.forge.addon.text.highlight.scanner.YAMLScanner;

public class Syntax
{
   public static void builtIns()
   {
      Scanner.Factory.registrer(PlainScanner.class);
      Scanner.Factory.registrer(JavaScanner.class);
      Scanner.Factory.registrer(HTMLScanner.class);
      Scanner.Factory.registrer(XMLScanner.class);
      Scanner.Factory.registrer(CSSScanner.class);
      Scanner.Factory.registrer(JavaScriptScanner.class);
      Scanner.Factory.registrer(JSONScanner.class);
      Scanner.Factory.registrer(PropertiesScanner.class);
      Scanner.Factory.registrer(SQLScanner.class);
      Scanner.Factory.registrer(GroovyScanner.class);
      Scanner.Factory.registrer(YAMLScanner.class);


      Encoder.Factory.registrer(Encoder.Type.TERMINAL.name(), TerminalEncoder.class);
      Encoder.Factory.registrer(Encoder.Type.DEBUG.name(), DebugEncoder.class);
   }

   public static final class Builder
   {

      private String scannerType;
      private Scanner scanner;
      private Map<String, Object> scannerOptions;

      private String encoderType;
      private Encoder encoder;
      private Map<String, Object> encoderOptions;

      private OutputStream output = System.out;

      private Theme theme = defaultTheme();

      private Builder()
      {
      }

      public static Builder create()
      {
         return new Builder();
      }

      public Builder scannerType(String scannerType)
      {
         this.scannerType = scannerType;
         return this;
      }

      public Builder scanner(Scanner scanner)
      {
         this.scanner = scanner;
         return this;
      }

      public Builder scannerOptions(Map<String, Object> options)
      {
         this.scannerOptions = options;
         return this;
      }

      public Builder encoderType(Encoder.Type encoderType)
      {
         return encoderType(encoderType.name());
      }

      public Builder encoderType(String encoderType)
      {
         this.encoderType = encoderType;
         return this;
      }

      public Builder encoder(Encoder encoder)
      {
         this.encoder = encoder;
         return this;
      }

      public Builder encoderOptions(Map<String, Object> options)
      {
         this.encoderOptions = options;
         return this;
      }

      public Builder output(OutputStream output)
      {
         this.output = output;
         return this;
      }

      public Builder theme(Theme theme)
      {
         this.theme = theme;
         return this;
      }

      public void execute(String source)
      {
         execute(new StringScanner(source));
      }

      public void execute(StringScanner source)
      {
         if (output == null && encoder == null)
         {
            throw new IllegalArgumentException("Either output or encoder must be defined");
         }

         Scanner in = scanner;
         if (scanner == null)
         {
            if (scannerType == null)
            {
               throw new IllegalArgumentException("Either input or inputType must be defined");
            }
            in = Scanner.Factory.byType(scannerType);
         }
         Encoder out = encoder;
         if (encoder == null)
         {
            if (encoderType == null)
            {
               throw new IllegalArgumentException("Either output or outputType must be defined");
            }
            out = Encoder.Factory.create(encoderType, output, theme, encoderOptions == null ? Options.create()
                     : encoderOptions);
         }
         in.scan(source, out, scannerOptions == null ? Options.create() : scannerOptions);
      }
   }

   public static Theme defaultTheme()
   {
      return new Theme()
               .set(Color.RED, TokenType.predefined_constant, TokenType.content, TokenType.delimiter, TokenType.color,
                        TokenType.value, TokenType.integer, TokenType.float_)
               .set(Color.CYAN, TokenType.tag, TokenType.class_, TokenType.function)
               .set(Color.MAGENTA, TokenType.keyword)
               .set(Color.GREEN, TokenType.type, TokenType.directive, TokenType.string, TokenType.attribute_value,
                        TokenType.attribute_name, TokenType.key);
   }

   public static void main(String[] args)
   {
      Syntax.builtIns();
      if (args.length < 1)
      {
         System.out.println("Usage: java -jar forge-text-syntax.jar file-name");
      }

      Encoder.Type encoder = Encoder.Type.TERMINAL;
      String fileName = args[0];
      if (args.length == 2)
      {
         encoder = Encoder.Type.DEBUG;
      }
      Scanner scanner = Scanner.Factory.byFileName(fileName);
      if (scanner == null)
      {
         throw new RuntimeException("Could not determine scanner type based on filename " + fileName);
      }

      String content = null;
      try
      {
         content = new String(Files.readAllBytes(Paths.get(fileName)));
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not read given file " + fileName, e);
      }

      BufferedOutputStream out = new BufferedOutputStream(System.out);
      Builder.create()
               .scanner(scanner)
               .encoderType(encoder)
               .output(out)
               .execute(content);

      try
      {
         out.flush();
      }
      catch (IOException e)
      {
      }
   }
}
