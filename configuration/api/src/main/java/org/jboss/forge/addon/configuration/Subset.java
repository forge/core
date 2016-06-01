/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a subset {@link Configuration} during injection
 * 
 * Eg:
 * 
 * <pre>
 * &#064;Inject
 * &#064;Subset(&quot;jira&quot;)
 * Configuration configuration;
 * </pre>
 * 
 * is the equivalent of
 * 
 * <pre>
 * &#064;Inject
 * Configuration configuration;
 * </pre>
 * 
 * and calling
 * 
 * <pre>
 * configuration.subset(&quot;jira&quot;);
 * </pre>
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subset
{
   String value();
}
