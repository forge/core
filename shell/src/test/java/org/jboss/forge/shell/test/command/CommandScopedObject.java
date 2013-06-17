/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.test.command;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jboss.forge.shell.command.CommandScoped;

@CommandScoped
public class CommandScopedObject
{
   private static final AtomicInteger COUNTER = new AtomicInteger();
   private int value;

   public CommandScopedObject()
   {
   }

   @PostConstruct
   void create()
   {
      value = COUNTER.incrementAndGet();

   }

   public int getValue()
   {
      return value;
   }

   @PreDestroy
   void destroy()
   {
      COUNTER.decrementAndGet();
   }
}
