/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.widgetbuilder;

import org.metawidget.statically.faces.component.ValueHolder;
import org.metawidget.statically.faces.component.html.CoreWidget;

/**
 * @author Richard Kennard
 */

public class SetPropertyActionListener
	extends CoreWidget
	implements ValueHolder {

	//
	// Constructor
	//

	public SetPropertyActionListener() {

		super( "setPropertyActionListener" );
	}

	//
	// Public methods
	//

   @Override
   public String getValue() {

      return getAttribute( "value" );
   }

   @Override
   public void setValue( String value ) {

      putAttribute( "value", value );
   }

   @Override
   public void setConverter( String value ) {

      putAttribute( "converter", value );
   }
}
