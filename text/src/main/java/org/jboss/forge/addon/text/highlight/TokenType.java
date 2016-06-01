/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

public enum TokenType
{
   debug, // highlight for debugging (white on blue background)

   annotation, // Groovy, Java
   attribute_name, // HTML, CSS
   attribute_value, // HTML
   binary, // Python, Ruby
   char_, // most scanners, also inside of strings
   class_, // lots of scanners, for different purposes also in CSS
   class_variable, // Ruby, YAML
   color, // CSS
   comment, // most scanners
   constant, // PHP, Ruby
   content, // inside of strings, most scanners
   decorator, // Python
   definition, // CSS
   delimiter, // inside strings, comments and other types
   directive, // lots of scanners
   doctype, // Goorvy, HTML, Ruby, YAML
   docstring, // Python
   done, // Taskpaper
   entity, // HTML
   error, // invalid token, most scanners
   escape, // Ruby (string inline variables like //$foo, //@bar)
   exception, // Java, PHP, Python
   filename, // Diff
   float_, // most scanners
   function, // CSS, JavaScript, PHP
   method, // groovy
   global_variable, // Ruby, YAML
   hex, // hexadecimal number; lots of scanners
   id, // CSS
   imaginary, // Python
   important, // CSS, Taskpaper
   include, // C, Groovy, Java, Python, Sass
   inline, // nested code, eg. inline string evaluation; lots of scanners
   inline_delimiter, // used instead of :inline > :delimiter FIXME: Why use inline_delimiter?
   instance_variable, // Ruby
   integer, // most scanners
   key, // lots of scanners, used together with :value
   keyword, // reserved word that's actually implemented; most scanners
   label, // C, PHP
   local_variable, // local and magic variables; some scanners
   map, // Lua tables
   modifier, // used inside on strings; lots of scanners
   namespace, // Clojure, Java, Taskpaper
   octal, // lots of scanners
   predefined, // predefined function: lots of scanners
   predefined_constant, // lots of scanners
   predefined_type, // C, Java, PHP
   preprocessor, // C, Delphi, HTML
   pseudo_class, // CSS
   regexp, // Groovy, JavaScript, Ruby
   reserved, // most scanners
   shell, // Ruby
   string, // most scanners
   symbol, // Clojure, Ruby, YAML
   tag, // CSS, HTML
   type, // CSS, Java, SQL, YAML
   value, // used together with :key; CSS, JSON, YAML
   variable, // Sass, SQL, YAML

   change, // Diff
   delete, // Diff
   head, // Diff, YAML
   insert, // Diff
   eyecatcher, // Diff

   ident, // almost all scanners
   operator, // almost all scanners

   space, // almost all scanners
   plain, // almost all scanners
   unknown
}
