package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;

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
   public Set<Class<?>> getServices()
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
   public Set<ExportedInstance<Object>> getExportedInstances(String typeName)
   {
      // no-op
      return Collections.emptySet();
   }

}
