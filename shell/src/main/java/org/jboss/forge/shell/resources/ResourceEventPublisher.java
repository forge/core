/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.resources;

import javax.enterprise.event.Observes;

import org.jboss.forge.resources.events.ResourceCreated;
import org.jboss.forge.resources.events.ResourceDeleted;
import org.jboss.forge.resources.events.ResourceModified;
import org.jboss.forge.resources.events.ResourceRenamed;
import org.jboss.forge.shell.ShellPrintWriter;

/**
 * Publishes file change events to the shell output stream.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceEventPublisher
{
   public void created(@Observes final ResourceCreated event, final ShellPrintWriter writer)
   {
      writer.println("Wrote " + event.getResource().getFullyQualifiedName());
   }

   public void modified(@Observes final ResourceModified event, final ShellPrintWriter writer)
   {
      writer.println("Wrote " + event.getResource().getFullyQualifiedName());
   }

   public void deleted(@Observes final ResourceDeleted event, final ShellPrintWriter writer)
   {
      writer.println("Deleted " + event.getResource().getFullyQualifiedName());
   }

   public void moved(@Observes final ResourceRenamed event, final ShellPrintWriter writer)
   {
      writer.println("Renamed " + event.getOriginalLocation() + " -> " + event.getNewLocation());
   }
}
