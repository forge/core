/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

/**
 * Defines a class as having a weight for ordering.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Weighted
{
   int DEFAULT = 0;
   int HIGH = 1000;
   int MEDIUM = 100;
   int LOW = -100;

   int priority();
}