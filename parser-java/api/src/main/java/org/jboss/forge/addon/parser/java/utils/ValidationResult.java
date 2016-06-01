/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.utils;

public class ValidationResult
{

   private final ResultType type;
   private final String message;

   public ValidationResult(ResultType type)
   {
      this(type, null);
   }

   public ValidationResult(ResultType type, String message)
   {
      this.type = type;
      this.message = message;
   }

   public ResultType getType()
   {
      return type;
   }

   public String getMessage()
   {
      return message;
   }
}
