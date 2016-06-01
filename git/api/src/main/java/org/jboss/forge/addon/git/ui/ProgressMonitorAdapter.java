/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import org.eclipse.jgit.lib.ProgressMonitor;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * Adapter for {@link ProgressMonitor} to {@link UIProgressMonitor} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProgressMonitorAdapter implements ProgressMonitor
{
   private final UIProgressMonitor monitor;

   public ProgressMonitorAdapter(UIProgressMonitor monitor)
   {
      this.monitor = monitor;
   }

   @Override
   public void start(int totalTasks)
   {
      monitor.beginTask("Executing Git command", totalTasks);
   }

   @Override
   public void beginTask(String title, int totalWork)
   {
      monitor.beginTask(title, totalWork);
   }

   @Override
   public void update(int completed)
   {
      monitor.worked(completed);
   }

   @Override
   public void endTask()
   {
      monitor.done();
   }

   @Override
   public boolean isCancelled()
   {
      return monitor.isCancelled();
   }

}
