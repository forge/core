/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.impl.UIInputImpl;


/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Exported
public class FooCommand implements UICommand {

    private UIInput<String> name;
    @Override
    public void initializeUI(UIContext context) throws Exception {
        name = new UIInputImpl<String>("foo", String.class);
        name.setLabel("foo");
        name.setRequired(true);

        context.getUIBuilder().add(name);
    }

    @Override
    public void validate(UIValidationContext context) {
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        return Result.success("boo");
    }
}
