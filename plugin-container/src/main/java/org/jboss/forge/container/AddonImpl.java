package org.jboss.forge.container;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;

public class AddonImpl implements Addon
{
   private Module module;
   private ServiceRegistry registry;
   private Status status = Status.STOPPED;
   private AddonEntry entry;

   public AddonImpl(AddonEntry entry, Module module)
   {
      this.entry = entry;
      this.module = module;
   }

   public String getId()
   {
      return entry.toModuleId();
   }

   public ClassLoader getClassLoader()
   {
      return module.getClassLoader();
   }

   public Module getModule()
   {
      return module;
   }

   public ServiceRegistry getServiceRegistry()
   {
      return registry;
   }

   public void setServiceRegistry(ServiceRegistry registry)
   {
      this.registry = registry;
   }

   @Override
   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }

   @Override
   public String toString()
   {
      return getId();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((entry == null) ? 0 : entry.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonImpl other = (AddonImpl) obj;
      if (entry == null)
      {
         if (other.entry != null)
            return false;
      }
      else if (!entry.equals(other.entry))
         return false;
      return true;
   }

}
