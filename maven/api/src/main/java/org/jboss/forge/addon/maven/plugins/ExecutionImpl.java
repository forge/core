/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.ArrayList;
import java.util.List;

public class ExecutionImpl implements Execution
{
   private String id;
   private String phase;
   private List<String> goals = new ArrayList<String>();
   private Configuration configuration;

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public String getPhase()
   {
      return phase;
   }

   @Override
   public List<String> getGoals()
   {
      return goals;
   }

   @Override
   public Configuration getConfig()
   {
      return configuration;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setPhase(String phase)
   {
      this.phase = phase;
   }

   public void addGoal(String goal)
   {
      goals.add(goal);
   }

   public void setConfiguration(Configuration configuration)
   {
      this.configuration = configuration;
   }

   @Override
   public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("<execution>")
               .append("<id>").append(id).append("</id>")
               .append("<phase>").append(phase).append("</phase>");
      for (String goal : goals)
      {
         b.append("<goal>").append(goal).append("</goal>");
      }

      if (configuration != null)
      {
         b.append(configuration.toString());
      }

      b.append("</execution>");

      return b.toString();
   }
}
