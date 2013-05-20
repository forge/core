/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.wizard;

/**
 * Marker interface for wizard steps.
 *
 * Classes that implement this interface are not considered starting entry points, hence MUST not be shown in the
 * available addon list
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface UIWizardStep extends UIWizard
{
}
