/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
public class DefaultResourceEventPublisher
{
   public void created(@Observes final ResourceCreated event, final ShellPrintWriter writer)
   {
      writer.println("Created " + event.getResource().getFullyQualifiedName());
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
