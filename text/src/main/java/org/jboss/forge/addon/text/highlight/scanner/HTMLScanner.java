/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Options;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.jboss.forge.addon.text.highlight.WordList;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/html.rb
 * Last update sha: 8c3c0c49a98eb8daceb69d0b233d054fbbccc49e
 */
public class HTMLScanner implements Scanner
{

   public static final String[] EVENT_ATTRIBUTES = new String[] {
            "onabort", "onafterprint", "onbeforeprint", "onbeforeunload", "onblur", "oncanplay",
            "oncanplaythrough", "onchange", "onclick", "oncontextmenu", "oncuechange", "ondblclick",
            "ondrag", "ondragdrop", "ondragend", "ondragenter", "ondragleave", "ondragover",
            "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", "onfocus",
            "onformchange", "onforminput", "onhashchange", "oninput", "oninvalid", "onkeydown",
            "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata", "onloadstart",
            "onmessage", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup",
            "onmousewheel", "onmove", "onoffline", "ononline", "onpagehide", "onpageshow", "onpause",
            "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onreadystatechange",
            "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onshow",
            "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload",
            "onvolumechange", "onwaiting" };

   public static final Pattern ATTR_NAME = Pattern.compile("[\\w.:-]+");
   public static final Pattern TAG_END = Pattern.compile("\\/?>");
   public static final Pattern HEX = Pattern.compile("[0-9a-fA-F]");
   public static final Pattern ENTITY = Pattern.compile("&(?:\\w+|\\#(?:\\d+|x" + HEX.pattern() + "+));");

   public static final Pattern SPACE = Pattern.compile("\\s+", Pattern.DOTALL);
   public static final Pattern CDATA_START = Pattern.compile("<!\\[CDATA\\[");
   public static final Pattern CDATA_END = Pattern.compile(".*?\\]\\]>", Pattern.DOTALL);
   public static final Pattern CDATA_ERROR = Pattern.compile(".+");
   public static final Pattern COMMENT = Pattern.compile("<!--(?:.*?-->|.*)", Pattern.DOTALL);
   public static final Pattern DOCTYPE = Pattern.compile("<!(\\w+)(?:.*?>|.*)|\\]>", Pattern.DOTALL);
   public static final Pattern PRE_PROCESSOR = Pattern.compile("<\\?xml(?:.*?\\?>|.*)");
   public static final Pattern COMMENT2 = Pattern.compile("<\\?(?:.*?\\?>|.*)", Pattern.DOTALL);
   public static final Pattern TAG = Pattern.compile("<\\/[-\\w.:]*>?", Pattern.DOTALL);
   public static final Pattern SPECIAL_TAG = Pattern.compile("<(?:(script|style)|[-\\w.:]+)(>)?", Pattern.DOTALL);
   public static final Pattern PLAIN = Pattern.compile("[^<>&]+");
   public static final Pattern ERROR = Pattern.compile("[<>&]");
   public static final Pattern EQUAL = Pattern.compile("=");
   public static final Pattern QUOTE = Pattern.compile("[\"']");
   public static final Pattern JAVASCRIPT_INLINE = Pattern.compile("javascript:[ \\t]*");
   public static final Pattern AMP = Pattern.compile("&");
   public static final Pattern END = Pattern.compile("[\\n>]");
   public static final Pattern SPECIAL_SPACE = Pattern.compile("[ \\t]*\\n");
   public static final Pattern SPECIAL_COMMENT = Pattern.compile("(\\s*<!--)(?:(.*?)(-->)|(.*))", Pattern.DOTALL);

   public static final Map<String, Pattern> PLAIN_STRING_CONTENT = new HashMap<String, Pattern>();
   {
      PLAIN_STRING_CONTENT.put("'", Pattern.compile("[^&'>\\n]+"));
      PLAIN_STRING_CONTENT.put("\"", Pattern.compile("[^&\">\\n]+"));
   }

   public enum EmbeddedType
   {
      script,
      style
   }

   public enum State
   {
      initial,
      in_special_tag,
      attribute,
      attribute_equal,
      attribute_value,
      attribute_value_string
   }

   public static final WordList<EmbeddedType> IN_ATTRIBUTE = new WordList<EmbeddedType>(null, true)
            .add(EVENT_ATTRIBUTES, EmbeddedType.script)
            .add(new String[] { "style" }, EmbeddedType.style);

   public static final Type TYPE = new Type("HTML", "\\.(html|htm|xhtml)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      State state = State.initial;
      EmbeddedType in_attribute = null;
      String in_tag = null;
      Pattern plain_string_content = null;

      while (source.hasMore())
      {
         MatchResult m = null;

         if (state != State.in_special_tag && (m = source.scan(SPACE)) != null)
         {
            encoder.textToken(m.group(), TokenType.space);
         }
         else
         {

            switch (state)
            {
            case initial:

               if ((m = source.scan(CDATA_START)) != null)
               {
                  encoder.textToken(m.group(), TokenType.inline_delimiter);
                  if ((m = source.scan(CDATA_END)) != null)
                  {
                     encoder.textToken(m.group().substring(0, m.group().length() - 3), TokenType.plain);
                     encoder.textToken("]]>", TokenType.inline_delimiter);
                  }
                  else if ((m = source.scan(CDATA_ERROR)) != null)
                  {
                     encoder.textToken(m.group(), TokenType.error);
                  }
               }
               else if ((m = source.scan(COMMENT)) != null)
               {
                  encoder.textToken(m.group(), TokenType.comment);
               }
               else if ((m = source.scan(DOCTYPE)) != null)
               {
                  encoder.textToken(m.group(), TokenType.doctype);
               }
               else if ((m = source.scan(PRE_PROCESSOR)) != null)
               {
                  encoder.textToken(m.group(), TokenType.preprocessor);
               }
               else if ((m = source.scan(COMMENT2)) != null)
               {
                  encoder.textToken(m.group(), TokenType.comment);
               }
               else if ((m = source.scan(TAG)) != null)
               {
                  in_tag = null;
                  encoder.textToken(m.group(), TokenType.tag);
               }
               else if ((m = source.scan(SPECIAL_TAG)) != null)
               {
                  encoder.textToken(m.group(), TokenType.tag);
                  in_tag = m.group(1);
                  if (m.group(2) != null)
                  {
                     if (in_tag != null)
                     {
                        state = State.in_special_tag;
                     }
                  }
                  else
                  {
                     state = State.attribute;
                  }
               }
               else if ((m = source.scan(PLAIN)) != null)
               {
                  encoder.textToken(m.group(), TokenType.plain);
               }
               else if ((m = source.scan(ENTITY)) != null)
               {
                  encoder.textToken(m.group(), TokenType.entity);
               }
               else if ((m = source.scan(ERROR)) != null)
               {
                  in_tag = null;
                  encoder.textToken(m.group(), TokenType.error);
               }
               else
               {
                  throw new RuntimeException("[BUG] else-case reached with state " + state + " in " + getClass());
               }

               break;
            case attribute:

               if ((m = source.scan(TAG_END)) != null)
               {
                  encoder.textToken(m.group(), TokenType.tag);
                  in_attribute = null;
                  if (in_tag != null)
                  {
                     state = State.in_special_tag;
                  }
                  else
                  {
                     state = State.initial;
                  }
               }
               else if ((m = source.scan(ATTR_NAME)) != null)
               {
                  in_attribute = IN_ATTRIBUTE.lookup(m.group());
                  encoder.textToken(m.group(), TokenType.attribute_name);
                  state = State.attribute_equal;
               }
               else
               {
                  in_tag = null;
                  encoder.textToken(source.next(), TokenType.error);
               }

               break;
            case attribute_equal:

               if ((m = source.scan(EQUAL)) != null)
               {
                  encoder.textToken(m.group(), TokenType.operator);
                  state = State.attribute_value;
               }
               else
               {
                  state = State.attribute;
                  break;
               }

            case attribute_value:
               if ((m = source.scan(ATTR_NAME)) != null)
               {
                  encoder.textToken(m.group(), TokenType.attribute_value);
                  state = State.attribute;
               }
               else if ((m = source.scan(QUOTE)) != null)
               {
                  if (EmbeddedType.script == in_attribute || EmbeddedType.style == in_attribute)
                  {
                     encoder.beginGroup(TokenType.string);
                     encoder.textToken(m.group(), TokenType.delimiter);
                     String groupStart = m.group();

                     if ((m = source.scan(JAVASCRIPT_INLINE)) != null)
                     {
                        encoder.textToken(m.group(), TokenType.comment);
                     }
                     String code = source.scanUntil(Pattern.compile("(?=" + groupStart + "|\\z)")).group();
                     if (EmbeddedType.script == in_attribute)
                     {
                        Syntax.Builder.create()
                                 .scannerType(JavaScriptScanner.TYPE.getName())
                                 .encoder(encoder)
                                 .execute(code);
                     }
                     else
                     {
                        Syntax.Builder.create()
                                 .scannerType(CSSScanner.TYPE.getName())
                                 .encoder(encoder)
                                 .scannerOptions(
                                          Options.create()
                                                   .add(CSSScanner.OPTION_START_STATE, CSSScanner.State.block))
                                 .execute(code);
                     }
                     m = source.scan(QUOTE);
                     if (m != null)
                     {
                        encoder.textToken(m.group(), TokenType.delimiter);
                     }
                     encoder.endGroup(TokenType.string);
                     state = State.attribute;
                     in_attribute = null;
                  }
                  else
                  {
                     encoder.beginGroup(TokenType.string);
                     state = State.attribute_value_string;
                     plain_string_content = PLAIN_STRING_CONTENT.get(m.group());
                     encoder.textToken(m.group(), TokenType.delimiter);
                  }
               }
               else if ((m = source.scan(TAG_END)) != null)
               {
                  encoder.textToken(m.group(), TokenType.tag);
                  state = State.initial;
               }
               else
               {
                  encoder.textToken(source.next(), TokenType.error);
               }
               break;
            case attribute_value_string:

               if ((m = source.scan(plain_string_content)) != null)
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else if ((m = source.scan(QUOTE)) != null)
               {
                  encoder.textToken(m.group(), TokenType.delimiter);
                  encoder.endGroup(TokenType.string);
                  state = State.attribute;
               }
               else if ((m = source.scan(ENTITY)) != null)
               {
                  encoder.textToken(m.group(), TokenType.entity);
               }
               else if ((m = source.scan(AMP)) != null)
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else if ((m = source.scan(END)) != null)
               {
                  encoder.endGroup(TokenType.string);
                  state = State.initial;
                  encoder.textToken(m.group(), TokenType.error);
               }
               break;
            case in_special_tag:

               if ("script".equalsIgnoreCase(in_tag) || "style".equalsIgnoreCase(in_tag))
               {
                  String code = null;
                  String closing = null;
                  if ((m = source.scan(SPECIAL_SPACE)) != null)
                  {
                     encoder.textToken(m.group(), TokenType.space);
                  }
                  if ((m = source.scan(SPECIAL_COMMENT)) != null)
                  {
                     code = m.group(2);
                     if (code == null)
                     {
                        code = m.group(4);
                     }
                     closing = m.group(3);
                     encoder.textToken(m.group(1), TokenType.comment);
                  }
                  else
                  {
                     code = source.scanUntil("(?=(?:\\n\\s*)?<\\/" + in_tag + ">)|\\z").group();
                     closing = null;
                  }
                  if (code != null && !code.isEmpty())
                  {
                     encoder.beginGroup(TokenType.inline);
                     if ("script".equalsIgnoreCase(in_tag))
                     {
                        Syntax.Builder.create()
                                 .scannerType(JavaScriptScanner.TYPE.getName())
                                 .encoder(encoder)
                                 .execute(code);
                     }
                     else
                     {
                        Syntax.Builder.create()
                                 .scannerType(CSSScanner.TYPE.getName())
                                 .encoder(encoder)
                                 .execute(code);
                     }
                     encoder.endGroup(TokenType.inline);
                  }
                  if (closing != null)
                  {
                     encoder.textToken(closing, TokenType.comment);
                  }
                  state = State.initial;
               }
               else
               {
                  throw new RuntimeException("unknown special tag " + in_tag);
               }
               break;
            default:
               throw new RuntimeException("Unknown state " + state);
            }
         }
      }
   }

}
