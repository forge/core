/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * A {@link Facet} with a mutable {@link #getOrigin()}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface MutableOrigin<FACETED extends Faceted<?>> extends Facet<FACETED>
{
   /**
    * Set the {@link Faceted} origin to which this {@link Facet} belongs. Should only be set once, since each
    * {@link Faceted} instance receives its own unique instance of all compatible {@link Facet} types. This method must
    * be called before invoking any operations on {@code this} instance.
    */
   void setOrigin(FACETED origin);
}
