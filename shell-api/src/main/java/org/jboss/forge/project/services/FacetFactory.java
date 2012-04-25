/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.project.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.bus.util.Annotations;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.AliasLiteral;
import org.jboss.forge.shell.project.FacetRegistry;
import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * Responsible for instantiating {@link Facet}s through CDI.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class FacetFactory
{
    private List<Facet> facets;
    private FacetRegistry registry;
    private BeanManager bm;

    @Inject
    public FacetFactory(FacetRegistry registry, BeanManager bm)
    {
        this.registry = registry;
        this.bm = bm;
    }

    public Set<Class<? extends Facet>> getFacetTypes()
    {
        return registry.getFacetTypes();
    }

    /**
     * @deprecated Use #getFacetTypes() instead. This will be removed in a future release.
     */
    public List<Facet> getFacets()
    {
        if (facets == null)
        {
            Iterator<Class<? extends Facet>> iterator = getFacetTypes().iterator();
            List<Facet> result = new ArrayList<Facet>();
            while (iterator.hasNext())
            {
                result.add(getFacet(iterator.next()));
            }
            facets = result;
        }
        return facets;
    }

    @SuppressWarnings("unchecked")
    public <T extends Facet> T getFacet(final Class<T> type) throws FacetNotFoundException
    {
        T result = null;

        for (Class<? extends Facet> facetType : getFacetTypes())
        {
            if (type.isAssignableFrom(facetType))
            {
                if (Annotations.isAnnotationPresent(facetType, Alias.class))
                    result = (T) BeanManagerUtils.getContextualInstance(bm, facetType,
                                new AliasLiteral(ConstraintInspector.getName(facetType)));
                else
                    result = (T) BeanManagerUtils.getContextualInstance(bm, facetType);
            }
            if (type.equals(facetType))
            {
                /**
                 * Ensure that if there is an exact match, we prefer it over other facet types.
                 */
                if (Annotations.isAnnotationPresent(facetType, Alias.class))
                    result = (T) BeanManagerUtils.getContextualInstance(bm, facetType,
                                new AliasLiteral(ConstraintInspector.getName(facetType)));
                else
                    result = (T) BeanManagerUtils.getContextualInstance(bm, facetType);
                break;
            }
        }

        if (result == null)
        {
            throw new FacetNotFoundException("The requested Facet of type [" + type.getName()
                        + "] could not be loaded.");
        }

        return result;
    }

    public Facet getFacetByName(final String facetName) throws FacetNotFoundException
    {
        Facet result = null;
        for (Class<? extends Facet> facet : getFacetTypes())
        {
            String name = ConstraintInspector.getName(facet);
            if (name.equals(facetName))
            {
                result = getFacet(facet);
                break;
            }
        }

        if (result == null)
        {
            throw new FacetNotFoundException("The requested Facet named [" + facetName + "] could not be found.");
        }

        return result;
    }
}
