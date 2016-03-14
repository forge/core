/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.concurrency;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractConcurrencyFacet extends AbstractJavaEEFacet implements ConcurrencyFacet
{
   @Inject
   public AbstractConcurrencyFacet(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "Concurrency";
   }

}
