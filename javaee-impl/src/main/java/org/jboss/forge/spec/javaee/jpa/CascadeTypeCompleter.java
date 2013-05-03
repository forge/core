package org.jboss.forge.spec.javaee.jpa;

import java.util.Arrays;

import javax.persistence.CascadeType;

import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class CascadeTypeCompleter extends SimpleTokenCompleter
{

   @Override
   public Iterable<?> getCompletionTokens()
   {
      return Arrays.asList(CascadeType.values());
   }

}
