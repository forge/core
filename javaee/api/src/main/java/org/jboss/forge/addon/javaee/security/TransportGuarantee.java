/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security;

/**
 * The value of the transport-guarantee element in the web.xml.
 * <p>
 * As defined in the Servlet specification, can be CONFIDENTIAL, INTEGRAL and NONE.
 *
 * @author <a href="mailto:ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
public enum TransportGuarantee
{
   CONFIDENTIAL, INTEGRAL, NONE
}
