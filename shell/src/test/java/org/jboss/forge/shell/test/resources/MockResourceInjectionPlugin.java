/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.resources;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("inject")
@Singleton
@RequiresResource(DirectoryResource.class)
public class MockResourceInjectionPlugin implements Plugin
{
   @Inject
   @Current
   private Resource<?> r;

   @Inject
   @Current
   private JavaResource j;

   @Inject
   @Current
   private DirectoryResource d;

   private Resource<?> observedResource;

   @DefaultCommand
   public void run()
   {

   }

   public void observe(@Observes final MockEvent event, @Current final Resource<?> resource)
   {
      this.observedResource = resource;
   }

   public Resource<?> getR()
   {
      return r;
   }

   public void setR(final Resource<?> r)
   {
      this.r = r;
   }

   public JavaResource getJ()
   {

      return j;
   }

   public void setJ(final JavaResource j)
   {
      this.j = j;
   }

   public DirectoryResource getD()
   {
      return d;
   }

   public void setD(final DirectoryResource d)
   {
      this.d = d;
   }

   public Resource<?> getObservedResource()
   {
      return observedResource;
   }

   public void setObservedResource(final Resource<?> observedResource)
   {
      this.observedResource = observedResource;
   }
}
