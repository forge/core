/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.commands;

import java.lang.annotation.ElementType;
import java.nio.file.attribute.AclEntryFlag;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.output.UIOutput;

public class ExampleAnnotatedCommand
{
   @Command(value = "Annotation Commands: Number 1", categories = { "Root", "Branch" })
   public String executeFromAnnotation(
            @Option(value = "name", label = "Field Name", required = true) String name,
            @Option(value = "elementType", label = "Element Type") ElementType elementType,
            @Option("anyData") String anyData)
   {
      System.out.println("EXECUTED FIELD with name =" + name + " and elementType=" + elementType);
      return "Hello there !";
   }

   @Command("Annotation Commands: Number 2")
   public void executeWithReservedParameters(
            @Option(value = "name", label = "Field Name", required = true) String name,
            UIOutput output,
            @Option("aclFlag") AclEntryFlag entryFlag,
            UIPrompt prompt)
   {
      if (prompt.promptBoolean("Display the values now?"))
      {
         output.out().printf("Values: %s - %s", name, entryFlag);
         output.out().println();
      }
      else
      {
         output.out().println("Ok, I will not display the values.");
      }
   }
   
   @Command(value = "Annotation Commands: Number 3", categories = { "Root", "Branch" })
   public String executeFromAnnotationWithNonAnnotatedParam(
            @Option(value = "name", label = "Field Name", required = true) String name,
            String notAnnotated,
            @Option(value = "elementType", label = "Element Type") ElementType elementType,
            @Option("anyData") String anyData)
   {
      System.out.println("EXECUTED FIELD with name =" + name + " and elementType=" + elementType);
      return "Hello there !";
   }
}
