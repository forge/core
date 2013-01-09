package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.Set;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;

public class AddonImpl implements Addon
{
   private Module module;
   private ServiceRegistry registry;
   private Status status;
   private final AddonId entry;
   private final Set<AddonDependency> dependencies;

   public AddonImpl(AddonId entry, Set<AddonDependency> dependencies)
   {
      this.entry = entry;
      this.dependencies = dependencies;
   }

   public AddonImpl(AddonId entry)
   {
      this(entry, Collections.<AddonDependency> emptySet());
   }

   public AddonImpl(AddonId entry, Status status)
   {
      this(entry);
      this.status = status;
   }

   @Override
   public AddonId getId()
   {
      return entry;
   }

   @Override
   public Set<AddonDependency> getDependencies()
   {
      return dependencies;
   }

   @Override
   public ClassLoader getClassLoader()
   {
      return module.getClassLoader();
   }

   public Module getModule()
   {
      return module;
   }

   public Addon setModule(Module module)
   {
      this.module = module;
      return this;
   }

   @Override
   public ServiceRegistry getServiceRegistry()
   {
      return registry;
   }

   public Addon setServiceRegistry(ServiceRegistry registry)
   {
      this.registry = registry;
      return this;
   }

   @Override
   public Status getStatus()
   {
      return status;
   }

   public Addon setStatus(Status status)
   {
      this.status = status;
      return this;
   }

   @Override
   public String toString()
   {
      return getId().toCoordinates() + (status == null ? "" : " - " + status);
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
