/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jms;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.JMSFacet;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@Alias("forge.spec.jms")
public class JmsFacetImpl extends BaseJavaEEFacet implements JMSFacet
{
    @Inject
    public JmsFacetImpl(DependencyInstaller installer)
    {
        super(installer);
    }

    @Override
    protected List<Dependency> getRequiredDependencies()
    {
        return Arrays.asList(
                (Dependency) DependencyBuilder.create("org.jboss.spec.javax.jms:jboss-jms-api_1.1_spec")
        );
    }
}
