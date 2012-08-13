/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jms;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.spec.javaee.JMSFacet;

import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@Alias("jms")
@RequiresProject
@RequiresFacet(DependencyFacet.class)
public class JmsPlugin implements Plugin
{
    @Inject
    private Project project;

    @Inject
    private Event<InstallFacets> request;

    @SetupCommand
    public void setup(final PipeOut out)
    {
        if (!project.hasFacet(JMSFacet.class))
        {
            request.fire(new InstallFacets(JMSFacet.class));
        }

        if (project.hasFacet(JMSFacet.class))
        {
            ShellMessages.success(out, "JMS is installed.");
        }
    }
}
