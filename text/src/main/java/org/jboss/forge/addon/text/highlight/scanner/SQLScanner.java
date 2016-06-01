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
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.jboss.forge.addon.text.highlight.WordList;

/*
 * https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/sql.rb
 * Last update sha: d3197be3f207f8fcf52954d8815a0ea1948d25a4
 *
 */
public class SQLScanner implements Scanner
{
   private static final String[] KEYWORDS = new String[] {
                              "all", "and", "any", "as", "before", "begin", "between", "by", "case", "check", "collate",
                              "each", "else", "end", "exists", "for", "foreign", "from", "full", "group", "having", "if",
                              "in", "inner", "is", "join", "like", "not", "of", "on", "or", "order", "outer", "over",
                              "references", "then", "to", "union", "using", "values", "when", "where", "left", "right", "distinct"};

   private static final String[] OBJECTS = new String[] {
                              "database", "databases", "table", "tables", "column", "columns", "fields", "index", "constraint",
                              "constraints", "transaction", "function", "procedure", "row", "key", "view", "trigger"};

   private static final String[] COMMANDS = new String[] {
                              "add", "alter", "comment", "create", "delete", "drop", "grant", "insert", "into", "select", "update",
                              "set", "show", "prompt", "begin", "commit", "rollback", "replace", "truncate"};

   private static final String[] PREDEFINED_TYPES = new String[] {
                              "char", "varchar", "varchar2", "enum", "binary", "text", "tinytext", "mediumtext",
                              "longtext", "blob", "tinyblob", "mediumblob", "longblob", "timestamp",
                              "date", "time", "datetime", "year", "double", "decimal", "float", "int",
                              "integer", "tinyint", "mediumint", "bigint", "smallint", "unsigned", "bit",
                              "bool", "boolean", "hex", "bin", "oct"};

   private static final String[] PREDEFINED_FUNCTIONS = new String[] {
                              "sum", "cast", "substring", "abs", "pi", "count", "min", "max", "avg", "now"};

   private static final String[] DIRECTIVES = new String[] {
                              "auto_increment", "unique", "default", "charset", "initially", "deferred", "deferrable", "cascade",
                              "immediate", "read", "write", "asc", "desc", "after", "primary", "foreign", "return", "engine"};

   private static final String[] PREDEFINED_CONSTANTS = new String[] {"null", "true", "false"};

   private static final WordList<TokenType> IDENT_KIND = new WordList<TokenType>(TokenType.ident, true)
            .add(KEYWORDS, TokenType.keyword)
            .add(OBJECTS, TokenType.type)
            .add(COMMANDS, TokenType.class_)
            .add(PREDEFINED_TYPES, TokenType.predefined_type)
            .add(PREDEFINED_CONSTANTS, TokenType.predefined_constant)
            .add(PREDEFINED_FUNCTIONS, TokenType.predefined)
            .add(DIRECTIVES, TokenType.directive);

   private static Pattern ESCAPE = Pattern.compile(" [rbfntv\\n\\\\\\/'\"] | x[a-fA-F0-9]{1,2} | [0-7]{1,3} | . ", Pattern.DOTALL|Pattern.COMMENTS);
   private static Pattern UNICODE_ESCAPE = Pattern.compile(" u[a-fA-F0-9]{4} | U[a-fA-F0-9]{8} ", Pattern.COMMENTS);
   private static Pattern STRING_PREFIXES = Pattern.compile("[xnb]|_\\w+", Pattern.CASE_INSENSITIVE);

   private static Pattern SPACE = Pattern.compile(" \\s+ | \\\\\\n ", Pattern.COMMENTS);
   private static Pattern COMMENT = Pattern.compile("(?:--\\s?|\\#).*", Pattern.COMMENTS);
   private static Pattern COMMENT_DIRECTIVE = Pattern.compile(" /\\* (!)? (?: .*? \\*/ | .* ) ", Pattern.DOTALL|Pattern.COMMENTS);
   private static Pattern OPERATOR = Pattern.compile(" [*\\/=<>:;,!&^|()\\[\\]{}~%] | [-+\\.](?!\\d) ", Pattern.COMMENTS);
   private static Pattern LETTER = Pattern.compile("[A-Za-z_]");
   private static Pattern STRING = Pattern.compile("(" + STRING_PREFIXES.pattern() + ")?([`\"'])", Pattern.CASE_INSENSITIVE);
   private static Pattern IDENT = Pattern.compile(" @? [A-Za-z_][A-Za-z_0-9]* ", Pattern.COMMENTS);
   private static Pattern HEX = Pattern.compile("0[xX][0-9A-Fa-f]+");
   private static Pattern OCTAL = Pattern.compile("0[0-7]+(?![89.eEfF])");
   private static Pattern INTEGER = Pattern.compile("[-+]?(?>\\d+)(?![.eEfF])");
   private static Pattern FLOAT = Pattern.compile("[-+]?(?:\\d[fF]|\\d*\\.\\d+(?:[eE][+-]?\\d+)?|\\d+[eE][+-]?\\d+)");
   private static Pattern PREDEFINED_CONSTANT = Pattern.compile("\\\\N");
   private static Pattern DELIMITER = Pattern.compile("[\"'`]");
   private static Pattern CHAR = Pattern.compile(" \\\\ (?: " + ESCAPE.pattern() + " | " + UNICODE_ESCAPE.pattern() + " ) ", Pattern.DOTALL|Pattern.COMMENTS);
   private static Pattern CONTENT = Pattern.compile(" \\\\ . ", Pattern.DOTALL|Pattern.COMMENTS);
   private static Pattern STRING_END = Pattern.compile(" \\\\ . ", Pattern.COMMENTS);

   private static Map<String, Pattern> STRING_CONTENT_PATTERN = new HashMap<String, Pattern>();
   {
     STRING_CONTENT_PATTERN.put("\"", Pattern.compile(" (?: [^\\\\\"] | \"\" )+ ", Pattern.COMMENTS));
     STRING_CONTENT_PATTERN.put("'", Pattern.compile(" (?: [^\\\\'] | '' )+ ", Pattern.COMMENTS));
     STRING_CONTENT_PATTERN.put("`", Pattern.compile(" (?: [^\\\\`] | `` )+ ", Pattern.COMMENTS));
   }

   public enum State
   {
      initial,
      string
   }

   public static final Type TYPE = new Type("SQL", "\\.(sql|ddl|dml|dcl)$");

   @Override
   public Type getType()
   {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      State state = State.initial;
      String stringType = null;
      boolean nameExpected = false;

      while (source.hasMore())
      {
         MatchResult m = null;

         switch (state)
         {
         case initial:

            if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
            }
            else if ((m = source.scan(COMMENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.comment);
            }
            else if ((m = source.scan(COMMENT_DIRECTIVE)) != null)
            {
               encoder.textToken(m.group(), m.group(1) != null ? TokenType.directive:TokenType.comment);
            }
            else if ((m = source.scan(OPERATOR)) != null)
            {
               if (m.group().equals(".") && source.check(LETTER) != null)
               {
                  nameExpected = true;
               }
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(STRING)) != null)
            {
               String prefix = m.group(1);
               stringType = m.group(2);
               encoder.beginGroup(TokenType.string);
               if (prefix != null)
               {
                  encoder.textToken(prefix, TokenType.modifier);
               }
               state = State.string;
               encoder.textToken(stringType, TokenType.delimiter);
            }
            else if ((m = source.scan(IDENT)) != null)
            {
               encoder.textToken(m.group(), nameExpected ? TokenType.ident:(m.group().startsWith("@") ? TokenType.variable:IDENT_KIND.lookup(m.group())));
               nameExpected = false;
            }
            else if ((m = source.scan(HEX)) != null)
            {
               encoder.textToken(m.group(), TokenType.hex);
            }
            else if ((m = source.scan(OCTAL)) != null)
            {
               encoder.textToken(m.group(), TokenType.octal);
            }
            else if ((m = source.scan(INTEGER)) != null)
            {
               encoder.textToken(m.group(), TokenType.integer);
            }
            else if ((m = source.scan(FLOAT)) != null)
            {
               encoder.textToken(m.group(), TokenType.float_);
            }
            else if ((m = source.scan(PREDEFINED_CONSTANT)) != null)
            {
               encoder.textToken(m.group(), TokenType.predefined_constant);
            }
            else {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;

         case string:
            if ((m = source.scan(STRING_CONTENT_PATTERN.get(stringType))) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(DELIMITER)) != null)
            {
               if (m.group().equals(stringType))
               {
                  if (source.peek(1).equals(stringType))
                  {
                     encoder.textToken(m.group() + source.next(), TokenType.content);
                  }
                  else
                  {
                     encoder.textToken(m.group(), TokenType.delimiter);
                     encoder.endGroup(TokenType.string);
                     state = State.initial;
                     stringType = null;
                  }
               }
               else
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
            }
            else if ((m = source.scan(CHAR)) != null)
            {
               encoder.textToken(m.group(), TokenType.char_);
            }
            else if ((m = source.scan(CONTENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(STRING_END)) != null)
            {
               if (m.group().length() != 0)
               {
                  encoder.textToken(m.group(), TokenType.error);
               }
               encoder.endGroup(TokenType.string);
               state = State.initial;
            }
            else
            {
               throw new RuntimeException("Else case reached; unhandled " + source.peek(1));
            }
           break;

         default:
            throw new RuntimeException("Unknown state " + state);
         }
      }
      if(state == State.string)
      {
         encoder.endGroup(TokenType.string);
      }
   }
}
