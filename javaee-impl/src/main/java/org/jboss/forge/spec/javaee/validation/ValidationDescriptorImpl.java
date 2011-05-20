/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.spec.javaee.validation;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.base.NodeProviderImplBase;
import org.jboss.shrinkwrap.descriptor.impl.base.XMLExporter;
import org.jboss.shrinkwrap.descriptor.spi.DescriptorExporter;
import org.jboss.shrinkwrap.descriptor.spi.Node;

/**
 * @author Kevin Pollet
 */
public class ValidationDescriptorImpl extends NodeProviderImplBase implements ValidationDescriptor
{
    private final Node model;

    public ValidationDescriptorImpl(String descriptorName)
    {
        this(descriptorName, new Node("validation-config")
                .attribute("xmlns", "http://jboss.org/xml/ns/javax/validation/configuration")
                .attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
    }

    public ValidationDescriptorImpl(String descriptorName, Node model)
    {
        super(descriptorName);
        this.model = model;
    }

    @Override
    public ValidationDescriptor defaultProvider(String defaultProvider)
    {
        model.getOrCreate("default-provider").text(defaultProvider);
        return this;
    }

    @Override
    public ValidationDescriptor messageInterpolator(String messageInterpolator)
    {
        model.getOrCreate("message-interpolator").text(messageInterpolator);
        return this;
    }

    @Override
    public ValidationDescriptor traversableResolver(String traversableResolver)
    {
        model.getOrCreate("traversable-resolver").text(traversableResolver);
        return this;
    }

    @Override
    public ValidationDescriptor constraintValidatorFactory(String constraintValidatorFactory)
    {
        model.getOrCreate("constraint-validator-factory").text(constraintValidatorFactory);
        return this;
    }

    @Override
    public ValidationDescriptor constraintMapping(String constraintMapping)
    {
        model.create("constraint-mapping").text(constraintMapping);
        return this;
    }

    @Override
    public String getDefaultProvider()
    {
        final Node defaultProvider = model.getSingle("default-provider");
        return defaultProvider == null ? null : defaultProvider.text();
    }

    @Override
    public String getMessageInterpolator()
    {
        final Node messageInterpolator = model.getSingle("message-interpolator");
        return messageInterpolator == null ? null : messageInterpolator.text();
    }

    @Override
    public String getTraversableResolver()
    {
        final Node traversableResolver = model.getSingle("traversable-resolver");
        return traversableResolver == null ? null : traversableResolver.text();
    }

    @Override
    public String getConstraintValidatorFactory()
    {
        final Node constraintValidatorFactory = model.getSingle("constraint-validator-factory");
        return constraintValidatorFactory == null ? null : constraintValidatorFactory.text();
    }

    @Override
    public List<String> getConstraintMappings()
    {
        List<String> mappings = new ArrayList<String>();
        for (Node oneNode : model.get("constraint-mapping"))
        {
            mappings.add(oneNode.text());
        }
        return mappings;
    }

    @Override
    public Node getRootNode()
    {
        return model;
    }

    @Override
    protected DescriptorExporter getExporter()
    {
        return new XMLExporter();
    }
}
