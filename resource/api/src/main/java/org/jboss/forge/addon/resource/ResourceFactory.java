/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessBean;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@Singleton
public class ResourceFactory implements Extension
{
   @Inject
   private Instance<BeanManager> managerInstance;

   private final List<ResourceGenerator> resourceGenerators = new ArrayList<ResourceGenerator>();

   /**
    * Most directories will tend to contain the same type of file (such as .java, .jar, .xml, etc). So we will remember
    * the last resource type we tested against and always re-try on subsequent queries before doing a comprehensive
    * match.
    */
   private volatile ResourceGenerator lastTypeLoaded = new ResourceGenerator()
   {
      @Override
      public boolean matches(final String name)
      {
         return false;
      }
   };

   public void setManager(@Observes final PostStartup event, final Instance<BeanManager> manager)
   {
      this.managerInstance = manager;
   }

   public void scan(@Observes final ProcessBean<Resource<?>> event, final BeanManager manager)
   {
      Bean<?> bean = event.getBean();
      Class<?> clazz = bean.getBeanClass();

      if (clazz.isAnnotationPresent(ResourceHandles.class))
      {
         for (String pspec : clazz.getAnnotation(ResourceHandles.class).value())
         {
            Pattern p = Pattern.compile(pathspecToRegEx(pspec));
            CreationalContext<?> creationalCtx = manager.createCreationalContext(bean);
            Resource<?> rInst = (Resource<?>) manager.getReference(bean, bean.getBeanClass(), creationalCtx);

            resourceGenerators.add(new ResourceGenerator(p, rInst));
         }
      }
   }

   @SuppressWarnings("unchecked")
   public <E, T extends Resource<E>> T createFromType(final Class<T> type, final E underlyingResource)
   {
      synchronized (this)
      {
         for (ResourceGenerator gen : resourceGenerators)
         {
            Resource<?> resource = gen.getResource();
            if (type.isAssignableFrom(resource.getClass()))
            {
               /*
                * This little <T> hack is required due to bug in javac:
                * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
                */
               T result = (lastTypeLoaded = gen).<T> getResource();
               return (T) result.createFrom(underlyingResource);
            }
         }
      }
      return null;
   }

   public Resource<File> getResourceFrom(File file)
   {
      /**
       * Special case for directories required.
       */
      file = file.getAbsoluteFile();
      if (file.isDirectory())
      {
         return new DirectoryResource(this, file);
      }

      final String name = file.getName();

      synchronized (this)
      {
         if (lastTypeLoaded.matches(name))
         {
            return lastTypeLoaded.getResource(File.class).createFrom(file);
         }

         for (ResourceGenerator gen : resourceGenerators)
         {
            if (gen.matches(name))
            {
               return (lastTypeLoaded = gen).getResource(File.class).createFrom(file);
            }
         }
      }

      return new UnknownFileResource(this, file);
   }

   public BeanManager getManagerInstance()
   {
      if (managerInstance != null)
      {
         return managerInstance.get();
      }
      return null;
   }

   static class ResourceGenerator
   {
      private Pattern pattern;
      private Resource<?> resource;

      ResourceGenerator()
      {
      }

      ResourceGenerator(final Pattern pattern, final Resource<?> resource)
      {
         this.pattern = pattern;
         this.resource = resource;

      }

      public boolean matches(final String name)
      {
         return pattern.matcher(name).matches();
      }

      @SuppressWarnings("unchecked")
      public <T> T getResource()
      {
         return (T) resource;
      }

      @SuppressWarnings({ "unchecked" })
      public <T> Resource<T> getResource(final Class<T> type)
      {
         return (Resource<T>) resource;
      }
   }

   private static String pathspecToRegEx(final String pathSpec)
   {
      return "^" + pathSpec.replaceAll("\\*", "\\.\\*").replaceAll("\\?", "\\.") + "$";
   }
}