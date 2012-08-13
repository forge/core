/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.project;

import org.jboss.forge.resources.Resource;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Declares a bean as being scoped to the current {@link Resource}. Beans using this scope will be destroyed when the
 * current {@link Resource} is changed. The scope is active as long as there is an active {@link Resource}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@NormalScope(passivating = false)
@Inherited
@Documented
@Target({ TYPE, METHOD, FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ResourceScoped
{

}
