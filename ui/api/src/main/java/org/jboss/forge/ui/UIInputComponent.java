/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui;

import java.util.concurrent.Callable;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.facets.Faceted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface UIInputComponent<IMPLTYPE, VALUETYPE> extends Faceted
{

   String getLabel();

   String getName();

   Class<VALUETYPE> getValueType();

   VALUETYPE getValue();

   boolean isEnabled();

   boolean isRequired();

   IMPLTYPE setDefaultValue(VALUETYPE value);

   IMPLTYPE setDefaultValue(Callable<VALUETYPE> callback);

   IMPLTYPE setEnabled(boolean b);

   IMPLTYPE setEnabled(Callable<Boolean> callable);

   IMPLTYPE setLabel(String label);

   IMPLTYPE setRequired(boolean required);

   IMPLTYPE setRequired(Callable<Boolean> required);

   IMPLTYPE setValue(VALUETYPE value);
}
