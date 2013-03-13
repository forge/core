/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.concurrent.locks.Lock;

import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.versions.VersionRange;

/**
 * An edge in the registered {@link Addon} graph.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonDependencyImpl implements AddonDependency
{
   private final Addon dependent;

   private boolean exported = false;
   private boolean optional = false;
   private Addon dependency;
   private VersionRange dependencyVersionRange;

   private LockManager lockManager;

   public AddonDependencyImpl(LockManager lockManager,
            Addon dependent, VersionRange dependencyVersionRange, Addon dependency, boolean exported, boolean optional)
   {
      super();
      this.lockManager = lockManager;
      this.dependent = dependent;
      this.dependencyVersionRange = dependencyVersionRange;
      this.dependency = dependency;
      this.exported = exported;
      this.optional = optional;
   }

   @Override
   public Addon getDependent()
   {
      return dependent;
   }

   @Override
   public Addon getDependency()
   {
      return dependency;
   }

   public void setDependency(Addon dependency)
   {
      Lock lock = lockManager.obtainLock(LockMode.WRITE);
      lock.lock();
      this.dependency = dependency;
      lock.unlock();
   }

   @Override
   public VersionRange getDependencyVersionRange()
   {
      return dependencyVersionRange;
   }

   @Override
   public boolean isExported()
   {
      return exported;
   }

   @Override
   public boolean isOptional()
   {
      return optional;
   }

}