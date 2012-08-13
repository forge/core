/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.shell.command.OptionMetadata;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandParserContext
{
   private boolean completing = false;
   private int paramCount;
   private final Map<OptionMetadata, Object> valueMap = new HashMap<OptionMetadata, Object>();
   private OptionMetadata lastParsed;
   private String lastParsedToken;
   private final List<String> warnings = new ArrayList<String>();
   private final List<String> ignoredTokens = new ArrayList<String>();
   private boolean finalTokenComplete = false;

   public CommandParserContext()
   {
   }

   public void incrementParmCount()
   {
      paramCount++;
   }

   public void addWarning(final String message)
   {
      if (!warnings.contains(message))
         warnings.add(message);
   }

   public void addIgnoredToken(final String token)
   {
      ignoredTokens.add(token);
   }

   public int getParamCount()
   {
      return paramCount;
   }

   @Override
   public String toString()
   {
      return "CommandParserContext [paramCount=" + paramCount + "]";
   }

   /**
    * Return an unmodifiable view of the parsed statement options.
    */
   public Map<OptionMetadata, Object> getValueMap()
   {
      return Collections.unmodifiableMap(valueMap);
   }

   public List<String> getWarnings()
   {
      return Collections.unmodifiableList(warnings);
   }

   public List<String> getIgnoredTokens()
   {
      return Collections.unmodifiableList(ignoredTokens);
   }

   /**
    * Return a count of how many ordered params have already been parsed.
    */
   public int getOrderedParamCount()
   {
      int result = 0;
      for (OptionMetadata option : valueMap.keySet())
      {
         if (option.isOrdered())
         {
            result++;
         }
      }
      return result;
   }

   public void put(final OptionMetadata option, final Object value, final String rawValue)
   {
      lastParsed = option;
      valueMap.put(option, value);
      lastParsedToken = rawValue;
   }

   /**
    * @return the last parsed {@link OptionMetadata}
    */
   public OptionMetadata getLastParsed()
   {
      return lastParsed;
   }

   public boolean isLastOptionValued()
   {
      return (lastParsed != null) && (valueMap.get(lastParsed) != null);
   }

   public boolean isEmpty()
   {
      return valueMap.isEmpty();
   }

   public String getLastParsedToken()
   {
      return lastParsedToken;
   }

   public void setCompleting(final boolean completing)
   {
      this.completing = completing;
   }

   public boolean isCompleting()
   {
      return completing;
   }

   public void setFinalTokenComplete(final boolean finalTokenComplete)
   {
      this.finalTokenComplete = finalTokenComplete;
   }

   public boolean isTokenComplete()
   {
      return finalTokenComplete;
   }
}
