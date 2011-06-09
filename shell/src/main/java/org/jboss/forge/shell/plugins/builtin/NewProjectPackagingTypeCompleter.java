package org.jboss.forge.shell.plugins.builtin;

import java.util.Arrays;

import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class NewProjectPackagingTypeCompleter extends SimpleTokenCompleter
{
   @Override
   public Iterable<?> getCompletionTokens()
   {
      return Arrays.asList(PackagingType.BASIC, PackagingType.JAR, PackagingType.WAR);
   }
}