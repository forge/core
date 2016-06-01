/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.progress;

/**
 * Default implementation for {@link UIProgressMonitor}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultUIProgressMonitor implements UIProgressMonitor
{
   private String taskName;
   private int currentWork;
   private int totalWork;
   private String subtask;
   private volatile boolean cancelled;

   @Override
   public void beginTask(String name, int totalWork)
   {
      this.taskName = name;
      this.totalWork = totalWork;
   }

   @Override
   public void done()
   {
      this.currentWork = totalWork;
   }

   @Override
   public boolean isCancelled()
   {
      return cancelled;
   }

   @Override
   public void setCancelled(boolean value)
   {
      this.cancelled = cancelled;
   }

   @Override
   public void setTaskName(String name)
   {
      this.taskName = name;
   }

   @Override
   public void subTask(String name)
   {
      this.subtask = name;
   }

   @Override
   public void worked(int work)
   {
      this.currentWork += work;
   }

   public String getTaskName()
   {
      return taskName;
   }

   public int getTotalWork()
   {
      return totalWork;
   }

   public int getCurrentWork()
   {
      return currentWork;
   }

   public String getSubtask()
   {
      return subtask;
   }
}
