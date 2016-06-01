/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.utils;

import static org.jboss.forge.addon.parser.java.utils.IdentifierType.CLASS_NAME;
import static org.jboss.forge.addon.parser.java.utils.IdentifierType.PACKAGE_NAME;
import static org.jboss.forge.addon.parser.java.utils.IdentifierType.VARIABLE_NAME;
import static org.jboss.forge.addon.parser.java.utils.ResultType.ERROR;
import static org.jboss.forge.addon.parser.java.utils.ResultType.INFO;
import static org.jboss.forge.addon.parser.java.utils.ResultType.WARNING;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.furnace.util.Strings;

public class JLSValidator
{

   private static final List<String> JAVA_KEYWORDS = Arrays.asList(
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "if", "package", "synchronized",
            "boolean", "do", "goto", "private", "this",
            "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while");

   private static final List<String> BOOLEAN_LITERALS = Arrays.asList("true", "false");

   private static final String NULL_LITERAL = "null";

   private JLSValidator()
   {
      // Not to be instantiated by users
   }

   /**
    * Validates whether the <code>identifier</code> parameter is a valid variable name.
    * 
    * @param identifier
    * @return
    */
   public static ValidationResult validateVariableName(String identifier)
   {
      if (Strings.isNullOrEmpty(identifier))
         return new ValidationResult(ERROR, Messages.notNullOrEmpty(VARIABLE_NAME));
      return validateIdentifier(identifier, IdentifierType.VARIABLE_NAME);
   }

   /**
    * Validates whether the <code>className</code> parameter is a valid class name. This method verifies both qualified
    * and unqualified class names.
    * 
    * @param className
    * @return
    */
   public static ValidationResult validateClassName(String className)
   {
      if (Strings.isNullOrEmpty(className))
         return new ValidationResult(ERROR, Messages.notNullOrEmpty(CLASS_NAME));
      int indexOfDot = className.lastIndexOf(".");
      if (indexOfDot == -1)
      {
         return validateIdentifier(className, CLASS_NAME);
      }
      else
      {
         String packageSequence = className.substring(0, indexOfDot);
         ValidationResult result = validatePackageName(packageSequence);
         if (!result.getType().equals(ResultType.INFO))
         {
            return result;
         }
         String classSequence = className.substring(indexOfDot + 1);
         return validateIdentifier(classSequence, CLASS_NAME);
      }
   }

   /**
    * Validates whether the <code>packageName</code> parameter is a valid package name. Note - this method does not
    * verify if the default package is valid or not.
    * 
    * @param packageName
    * @return
    */
   public static ValidationResult validatePackageName(String packageName)
   {
      if (Strings.isNullOrEmpty(packageName))
         return new ValidationResult(ERROR, Messages.notNullOrEmpty(PACKAGE_NAME));
      int indexOfDot = packageName.indexOf(".");
      if (indexOfDot == -1)
      {
         return validateIdentifier(packageName, PACKAGE_NAME);
      }
      else if (indexOfDot == 0)
      {
         return new ValidationResult(ERROR, "The package name must not start with a '.'");
      }
      else if (packageName.charAt(packageName.length() - 1) == '.')
      {
         return new ValidationResult(ERROR, "The package name must not end with a '.'");
      }
      else
      {
         String[] names = packageName.split("[.]");
         for (String name : names)
         {
            if (Strings.isNullOrEmpty(name))
            {
               return new ValidationResult(ERROR, Messages.isInvalidPackageName(packageName));
            }
            ValidationResult result = validateIdentifier(name, PACKAGE_NAME);
            if (!result.getType().equals(ResultType.INFO))
            {
               return result;
            }
         }
      }
      return new ValidationResult(INFO);
   }

   /**
    * Validates whether the <code>identifier</code> parameter is a valid identifier name for the <code>type</code>
    * parameter.
    * 
    * @param identifier
    * @param type
    * @return
    */
   public static ValidationResult validateIdentifier(String identifier, IdentifierType type)
   {
      if (Strings.isNullOrEmpty(identifier))
         return new ValidationResult(ERROR, Messages.notNullOrEmpty(type));
      if (isReservedWord(identifier))
      {
         return new ValidationResult(ERROR, Messages.isAJavaKeyWord(identifier));
      }
      int javaLetter = identifier.codePointAt(0);
      if (!Character.isJavaIdentifierStart(javaLetter))
      {
         return new ValidationResult(ERROR, Messages.containsInvalidCharacters(identifier));
      }
      for (int ctr = 1; ctr < identifier.length(); ctr++)
      {
         int javaLetterOrDigit = identifier.codePointAt(ctr);
         if (!Character.isJavaIdentifierPart(javaLetterOrDigit))
         {
            return new ValidationResult(ERROR, Messages.containsInvalidCharacters(identifier));
         }
      }
      if (CLASS_NAME.equals(type) && identifier.contains("$"))
      {
         return new ValidationResult(WARNING, "The use of '$' in class names is discouraged.");
      }
      else
      {
         return new ValidationResult(INFO);
      }
   }

   static class Messages
   {

      static String notNullOrEmpty(IdentifierType type)
      {
         StringBuilder builder = new StringBuilder();
         switch (type)
         {
         case CLASS_NAME:
            builder.append("Class name");
            break;
         case PACKAGE_NAME:
            builder.append("Package name");
            break;
         case VARIABLE_NAME:
            builder.append("Identifier");
            break;
         default:
            break;
         }
         builder.append(" cannot be empty");
         return builder.toString();
      }

      static String isAJavaKeyWord(String identifier)
      {
         return identifier + " is a keyword.";
      }

      static String containsInvalidCharacters(String identifier)
      {
         return identifier + " is not a valid Java identifier.";
      }

      static String isInvalidPackageName(String packageName)
      {
         return packageName + " is not a valid Java package.";
      }
   }

   public static boolean isReservedWord(String word)
   {
      return JAVA_KEYWORDS.contains(word) || BOOLEAN_LITERALS.contains(word) || NULL_LITERAL.equals(word);
   }
}
