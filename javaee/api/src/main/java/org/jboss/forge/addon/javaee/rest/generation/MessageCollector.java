/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects validation messages
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MessageCollector
{
   private Map<MessageType, List<String>> messages = new HashMap<MessageType, List<String>>();

   public void addErrorMessage(String message)
   {
      add(MessageType.ERROR, message);
   }

   public void addInformationMessage(String message)
   {
      add(MessageType.INFO, message);
   }

   public void addWarningMessage(String message)
   {
      add(MessageType.WARNING, message);
   }

   public List<String> getErrorMessages()
   {
      return get(MessageType.ERROR);
   }

   public List<String> getInformationMessages()
   {
      return get(MessageType.INFO);
   }

   public List<String> getWarningMessages()
   {
      return get(MessageType.WARNING);
   }

   private void add(MessageType type, String message)
   {
      List<String> list = messages.get(type);
      if (list == null)
      {
         list = new ArrayList<String>();
         messages.put(type, list);
      }
      list.add(message);

   }

   private List<String> get(MessageType type)
   {
      List<String> list = messages.get(type);
      return list == null ? Collections.<String> emptyList() : list;
   }

   private static enum MessageType
   {
      ERROR,
      INFO,
      WARNING;
   }

}
