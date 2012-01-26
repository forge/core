// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

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
