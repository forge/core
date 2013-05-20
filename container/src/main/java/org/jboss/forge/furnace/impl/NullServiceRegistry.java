package org.jboss.forge.furnace.impl;

import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Typed;

import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.services.ServiceRegistry;

/**
 * Used when an addon does not provide services.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Typed()
public class NullServiceRegistry implements ServiceRegistry
{

   @Override
   public <T> ExportedInstance<T> getExportedInstance(Class<T> serviceType)
   {
      // no-op
      return null;
   }

   @Override
   public <T> ExportedInstance<T> getExportedInstance(String serviceType)
   {
      // no-op
      return null;
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> serviceType)
   {
      // no-op
      return Collections.emptySet();
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(String clazz)
   {
      // no-op
      return Collections.emptySet();
   }

   @Override
   public boolean hasService(Class<?> serviceType)
   {
      // no-op
      return false;
   }

   @Override
   public boolean hasService(String clazz)
   {
      // no-op
      return false;
   }

   @Override
   public Set<Class<?>> getExportedTypes()
   {
      // no-op
      return Collections.emptySet();
   }

   @Override
   public <T> Set<Class<T>> getExportedTypes(Class<T> type)
   {
      // no-op
      return Collections.emptySet();
   }

}
