/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import javax.ws.rs.core.Application;

/**
 * Configuration types of JAX-RS
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum RestActivatorType
{
   /**
    * Configured via web.xml
    */
   WEB_XML,

   /**
    * Configured via an {@link Application}
    */
    APP_CLASS
}
