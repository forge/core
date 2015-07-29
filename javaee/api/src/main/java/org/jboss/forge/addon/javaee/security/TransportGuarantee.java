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
