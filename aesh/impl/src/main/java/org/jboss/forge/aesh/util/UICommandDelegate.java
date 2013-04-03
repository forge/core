/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.util;

import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.metadata.UICategory;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;

import javax.enterprise.inject.Vetoed;
import java.net.URL;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Vetoed
public class UICommandDelegate implements UICommand {

    private UICommand delegate;

    public UICommandDelegate(UICommand delegate) {
        this.delegate = delegate;
    }

    @Override
    public UICommandMetadata getMetadata() {
        if(delegate.getMetadata().getName().contains(" ")) {
            return new UICommandMetadataImpl( shellifyName( delegate.getMetadata().getName()),
                    delegate.getMetadata().getDescription(),
                    delegate.getMetadata().getCategory(),
                    delegate.getMetadata().getDocLocation());
        }
        else
            return delegate.getMetadata();
    }

    @Override
    public boolean isEnabled(UIContext context) {
        return delegate.isEnabled(context);
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        delegate.initializeUI(builder);
    }

    @Override
    public void validate(UIValidationContext validator) {
        delegate.validate(validator);
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        return delegate.execute(context);
    }

    private String shellifyName(String name) {
        return name.trim().toLowerCase().replaceAll("\\W+", "-");
    }

    class UICommandMetadataImpl implements UICommandMetadata {

        private String name;
        private String desc;
        private UICategory category;
        private URL loc;

        public UICommandMetadataImpl(String name, String desc,
                                         UICategory category, URL loc) {
            this.name = name;
            this.desc = desc;
            this.category = category;
            this.loc = loc;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return desc;
        }

        @Override
        public UICategory getCategory() {
            return category;
        }

        @Override
        public URL getDocLocation() {
            return loc;
        }
    }
}
