package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ExportedInstanceImpl;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

public class ServiceRegistryImpl implements ServiceRegistry
{
   private Set<Class<?>> services = Sets.getConcurrentSet();

   private BeanManager manager;

   private AddonImpl addon;

   public ServiceRegistryImpl(AddonImpl addon, BeanManager manager, ContainerServiceExtension extension)
   {
      this.addon = addon;
      this.manager = manager;

      for (Class<?> clazz : extension.getServices())
      {
         addService(clazz);
      }
   }

   public <T> void addService(Class<T> clazz)
   {
      services.add(clazz);
   }

   @Override
   public <T> ExportedInstance<T> getExportedInstance(Class<T> clazz)
   {
      ensureAddonStarted();
      try
      {
         if (!manager.getBeans(clazz).isEmpty())
            return new ExportedInstanceImpl<T>(addon.getClassLoader(), manager, clazz);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ExportedInstance<T> getExportedInstance(String clazz)
   {
      Class<?> type;
      try
      {
         type = Class.forName(clazz, true, addon.getClassLoader());
         return (ExportedInstance<T>) getExportedInstance(type);
      }
      catch (ClassNotFoundException e)
      {
         return null;
      }
   }

   @Override
   public Set<Class<?>> getServices()
   {
      return services;
   }

   @Override
   public boolean hasService(String clazz)
   {
      try
      {
         Class<?> type = Class.forName(clazz, true, addon.getClassLoader());
         return hasService(type);
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public boolean hasService(Class<?> clazz)
   {
      for (Class<?> service : services)
      {
         if (clazz.isAssignableFrom(service))
            return true;
      }
      return false;
   }

   @Override
   public String toString()
   {
      return services.toString();
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz)
   {
      Set<ExportedInstance<T>> result = new HashSet<ExportedInstance<T>>();
      for (Class<?> type : services)
      {
         if (clazz.isAssignableFrom(type))
         {
            result.add((ExportedInstance<T>) getExportedInstance(type));
         }
      }
      return result;
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(String clazz)
   {
      try
      {
         @SuppressWarnings("unchecked")
         Class<T> type = (Class<T>) Class.forName(clazz, true, addon.getClassLoader());
         return getExportedInstances(type);
      }
      catch (ClassNotFoundException e)
      {
         return Collections.emptySet();
      }
   }

   private void ensureAddonStarted()
   {
      try
      {
         addon.getFuture().get();
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon was not started.", e);
      }
   }
}
