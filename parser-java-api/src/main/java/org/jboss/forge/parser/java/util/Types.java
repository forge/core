/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.parser.java.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Types
{
   public static boolean areEquivalent(String left, String right)
   {
      if ((left == null) && (right == null))
         return true;
      if ((left == null) || (right == null))
         return false;
      if (left.equals(right))
         return true;

      left = left.replaceAll("^(.*)<.*>$", "$1");
      right = right.replaceAll("^(.*)<.*>$", "$1");

      String l = toSimpleName(left);
      String r = toSimpleName(right);

      String lp = getPackage(left);
      String rp = getPackage(right);

      if (l.equals(r))
      {
         if (!lp.isEmpty() && !rp.isEmpty())
         {
            return false;
         }
         return true;
      }

      return false;
   }

   public static String toSimpleName(final String fieldType)
   {
      String result = fieldType;
      if (result != null)
      {
         String[] tokens = tokenizeClassName(result);
         if (tokens != null)
         {
            result = tokens[tokens.length - 1];
         }
      }
      return result;
   }

   public static String[] tokenizeClassName(final String className)
   {
      String[] result = null;
      if (className != null)
      {
         result = className.split("\\.");
      }
      return result;
   }

   public static boolean isQualified(final String className)
   {
      String[] tokens = tokenizeClassName(className);
      return (tokens != null) && (tokens.length > 1);
   }

   public static String getPackage(final String className)
   {
      if (className.indexOf(".") > -1)
      {
         return className.substring(0, className.lastIndexOf("."));
      }
      return "";
   }

   public static boolean isSimpleName(final String name)
   {
      return (name != null) && name.matches("(?i)(?![0-9])[a-z0-9$_]+");
   }

   public static boolean isJavaLang(final String type)
   {
      for (String t : langTypes)
      {
         if (type.endsWith(t))
            return true;
      }
      return false;
   }

   static List<String> langTypes = Arrays.asList(
            // Interfaces
            "Appendable",
            "AutoCloseable",
            "CharSequence",
            "Cloneable",
            "Comparable",
            "Iterable",
            "Readable",
            "Runnable",
            // Classes
            "Boolean",
            "Byte",
            "Character",
            "Character.Subset",
            "Character.UnicodeBlock",
            "Class",
            "ClassLoader",
            "ClassValue",
            "Compiler",
            "Double",
            "Enum",
            "Float",
            "InheritableThreadLocal",
            "Integer",
            "Long",
            "Math",
            "Number",
            "Object",
            "Package",
            "Process",
            "ProcessBuilder",
            "ProcessBuilder.Redirect",
            "Runtime",
            "RuntimePermission",
            "SecurityManager",
            "Short",
            "StackTraceElement",
            "StrictMath",
            "String",
            "StringBuffer",
            "StringBuilder",
            "System",
            "Thread",
            "ThreadGroup",
            "ThreadLocal",
            "Throwable",
            "Void",
            // Exception Types
            "AbstractMethodError",
            "AssertionError",
            "BootstrapMethodError",
            "ClassCircularityError",
            "ClassFormatError",
            "Error",
            "ExceptionInInitializerError",
            "IllegalAccessError",
            "IncompatibleClassChangeError",
            "InstantiationError",
            "InternalError",
            "LinkageError",
            "NoClassDefFoundError",
            "NoSuchFieldError",
            "NoSuchMethodError",
            "OutOfMemoryError",
            "StackOverflowError",
            "ThreadDeath",
            "UnknownError",
            "UnsatisfiedLinkError",
            "UnsupportedClassVersionError",
            "VerifyError",
            "VirtualMachineError",
            // Errors
            "AbstractMethodError",
            "AssertionError",
            "BootstrapMethodError",
            "ClassCircularityError",
            "ClassFormatError",
            "Error",
            "ExceptionInInitializerError",
            "IllegalAccessError",
            "IncompatibleClassChangeError",
            "InstantiationError",
            "InternalError",
            "LinkageError",
            "NoClassDefFoundError",
            "NoSuchFieldError",
            "NoSuchMethodError",
            "OutOfMemoryError",
            "StackOverflowError",
            "ThreadDeath",
            "UnknownError",
            "UnsatisfiedLinkError",
            "UnsupportedClassVersionError",
            "VerifyError",
            "VirtualMachineError",
            // Annotation Types
            "Deprecated",
            "Override",
            "SafeVarargs",
            "SuppressWarnings"
            );

   public static boolean isGeneric(final String type)
   {
      return (type != null) && type.matches(".*<.*>$");
   }

   public static String stripGenerics(final String type)
   {
      if (isGeneric(type))
      {
         return type.replaceFirst("^([^<]*)<.*>$", "$1");
      }
      return type;
   }

   public static String getGenerics(final String type)
   {
      if (isGeneric(type))
      {
         return type.replaceFirst("^[^<]*(<.*>)$", "$1");
      }
      return "";
   }

}
