/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.json.resource;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * A {@link Resource} that represents a {@link JsonObject}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface JsonResource extends FileResource<JsonResource>
{
   /**
    * Returns a JSON array or object that is represented in the input source
    * 
    * Use this method if you are unsure of the json data format
    */
   JsonStructure getJsonStructure();

   /**
    * Return the {@link JsonArray} representing the underlying Json data
    */
   JsonArray getJsonArray();

   /**
    * Return the {@link JsonObject} representing the underlying Json data
    */
   JsonObject getJsonObject();

   /**
    * Return a {@link JsonObjectBuilder} pre-populated with the underlying Json data
    */
   JsonObjectBuilder getJsonObjectBuilder();

   /**
    * Return a {@link JsonArrayBuilder} pre-populated with the underlying Json data
    */
   JsonArrayBuilder getJsonArrayBuilder();

   /**
    * Sets the content to this {@link JsonStructure} (could be an array or object)
    */
   JsonResource setContents(JsonStructure structure);

}
