/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.List;

public class ExecutionBuilder implements Execution
{
   private ExecutionImpl execution;

   private ExecutionBuilder()
   {
      execution = new ExecutionImpl();
   }

   private ExecutionBuilder(ExecutionImpl execution)
   {
      this.execution = execution;
   }

   public static ExecutionBuilder create()
   {
      return new ExecutionBuilder();
   }

   public static ExecutionBuilder create(ExecutionImpl execution)
   {
      return new ExecutionBuilder(execution);
   }

   public ExecutionBuilder setId(String id)
   {
      execution.setId(id);
      return this;
   }

   public ExecutionBuilder setPhase(String phase)
   {
      execution.setPhase(phase);
      return this;
   }

   public ExecutionBuilder addGoal(String goal)
   {
      execution.addGoal(goal);
      return this;
   }

   @Override
   public String getId()
   {
      return execution.getId();
   }

   @Override
   public String getPhase()
   {
      return execution.getPhase();
   }

   @Override
   public List<String> getGoals()
   {
      return execution.getGoals();
   }

   @Override
   public String toString()
   {
      return execution.toString();
   }

   @Override
   public Configuration getConfig()
   {
      return execution.getConfig();
   }

   public ExecutionBuilder setConfig(Configuration configuration)
   {
      execution.setConfiguration(configuration);
      return this;
   }
}
