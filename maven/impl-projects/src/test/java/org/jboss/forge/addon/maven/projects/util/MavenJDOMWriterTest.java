/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenJDOMWriterTest
{

   @Test
   public void testKeepComments() throws Exception
   {
      Path path = Paths.get("src/test/resources/pom-template.xml");
      String pom = new String(Files.readAllBytes(path));
      Model model = new MavenXpp3Reader().read(new StringReader(pom));
      model.setArtifactId("mytest");
      SAXBuilder saxBuilder = new SAXBuilder();
      Document document = saxBuilder.build(new StringReader(pom));
      MavenJDOMWriter writer = new MavenJDOMWriter();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      writer.write(model, document, "UTF-8", new OutputStreamWriter(baos));
      Assert.assertEquals(pom.replace("myartifactid", "mytest").trim(), baos.toString().trim());
   }

}
