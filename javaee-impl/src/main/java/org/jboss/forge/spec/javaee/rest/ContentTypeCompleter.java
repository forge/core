/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class ContentTypeCompleter extends SimpleTokenCompleter
{

   @Override
   public Iterable<?> getCompletionTokens()
   {
      return Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
   }

}
