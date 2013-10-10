/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import org.jboss.shrinkwrap.descriptor.api.persistence21.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence21.PersistenceUnit;

/**
 * Performs configuration on a {@link JPADataSource} to ensure it is properly set up for the this implementation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface PersistenceContainer
{
   /**
    * Return the name of this container
    */
   String getName();

   /**
    * Validate against the supplied datastore
    */
   void validate(JPADataSource dataSource) throws Exception;

   /**
    * Set up the connection info.
    */
   PersistenceUnit<PersistenceDescriptor> setupConnection(PersistenceUnit<PersistenceDescriptor> unit,
            JPADataSource dataSource);

   /**
    * @return true if this {@link PersistenceContainer} supports JTA configuration
    */
   boolean isJTASupported();
}
