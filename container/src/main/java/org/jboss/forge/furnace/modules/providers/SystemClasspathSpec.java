package org.jboss.forge.furnace.modules.providers;

import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.ModuleIdentifier;

public class SystemClasspathSpec extends AbstractModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("javax.api");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("javax/accessibility");
      paths.add("javax/activity");
      paths.add("javax/crypto");
      paths.add("javax/crypto/interfaces");
      paths.add("javax/crypto/spec");
      paths.add("javax/imageio");
      paths.add("javax/imageio/event");
      paths.add("javax/imageio/metadata");
      paths.add("javax/imageio/plugins/bmp");
      paths.add("javax/imageio/plugins/jpeg");
      paths.add("javax/imageio/spi");
      paths.add("javax/imageio/stream");
      paths.add("javax/lang/model");
      paths.add("javax/lang/model/element");
      paths.add("javax/lang/model/type");
      paths.add("javax/lang/model/util");
      paths.add("javax/management");
      paths.add("javax/management/loading");
      paths.add("javax/management/modelmbean");
      paths.add("javax/management/monitor");
      paths.add("javax/management/openmbean");
      paths.add("javax/management/relation");
      paths.add("javax/management/remote");
      paths.add("javax/management/remote/rmi");
      paths.add("javax/management/timer");
      paths.add("javax/naming");
      paths.add("javax/naming/directory");
      paths.add("javax/naming/event");
      paths.add("javax/naming/ldap");
      paths.add("javax/naming/spi");
      paths.add("javax/net");
      paths.add("javax/net/ssl");
      paths.add("javax/print");
      paths.add("javax/print/attribute");
      paths.add("javax/print/attribute/standard");
      paths.add("javax/print/event");
      paths.add("javax/rmi/ssl");
      paths.add("javax/script");
      paths.add("javax/security/auth");
      paths.add("javax/security/auth/callback");
      paths.add("javax/security/auth/kerberos");
      paths.add("javax/security/auth/login");
      paths.add("javax/security/auth/spi");
      paths.add("javax/security/auth/x500");
      paths.add("javax/security/cert");
      paths.add("javax/security/sasl");
      paths.add("javax/sound/midi");
      paths.add("javax/sound/midi/spi");
      paths.add("javax/sound/sampled");
      paths.add("javax/sound/sampled/spi");
      paths.add("javax/sql");
      paths.add("javax/sql/rowset");
      paths.add("javax/sql/rowset/serial");
      paths.add("javax/sql/rowset/spi");
      paths.add("javax/swing");
      paths.add("javax/swing/border");
      paths.add("javax/swing/colorchooser");
      paths.add("javax/swing/event");
      paths.add("javax/swing/filechooser");
      paths.add("javax/swing/plaf");
      paths.add("javax/swing/plaf/basic");
      paths.add("javax/swing/plaf/metal");
      paths.add("javax/swing/plaf/multi");
      paths.add("javax/swing/plaf/nimbus");
      paths.add("javax/swing/plaf/synth");
      paths.add("javax/swing/table");
      paths.add("javax/swing/text");
      paths.add("javax/swing/text/html");
      paths.add("javax/swing/text/html/parser");
      paths.add("javax/swing/text/rtf");
      paths.add("javax/swing/tree");
      paths.add("javax/swing/undo");
      paths.add("javax/tools");
      paths.add("javax/xml");
      paths.add("javax/xml/crypto");
      paths.add("javax/xml/crypto/dom");
      paths.add("javax/xml/crypto/dsig");
      paths.add("javax/xml/crypto/dsig/dom");
      paths.add("javax/xml/crypto/dsig/keyinfo");
      paths.add("javax/xml/crypto/dsig/spec");
      paths.add("javax/xml/datatype");
      paths.add("javax/xml/namespace");
      paths.add("javax/xml/parsers");
      paths.add("javax/xml/stream");
      paths.add("javax/xml/stream/events");
      paths.add("javax/xml/stream/util");
      paths.add("javax/xml/transform");
      paths.add("javax/xml/transform/dom");
      paths.add("javax/xml/transform/sax");
      paths.add("javax/xml/transform/stax");
      paths.add("javax/xml/transform/stream");
      paths.add("javax/xml/validation");
      paths.add("javax/xml/xpath");
      paths.add("org/ietf/jgss");
      paths.add("org/w3c/dom");
      paths.add("org/w3c/dom/bootstrap");
      paths.add("org/w3c/dom/css");
      paths.add("org/w3c/dom/events");
      paths.add("org/w3c/dom/html");
      paths.add("org/w3c/dom/ranges");
      paths.add("org/w3c/dom/stylesheets");
      paths.add("org/w3c/dom/traversal");
      paths.add("org/w3c/dom/ls");
      paths.add("org/w3c/dom/views");
      paths.add("org/w3c/dom/xpath");
      paths.add("org/xml/sax");
      paths.add("org/xml/sax/ext");
      paths.add("org/xml/sax/helpers");
   }

   @Override
   protected ModuleIdentifier getId()
   {
      return ID;
   }

   @Override
   protected Set<String> getPaths()
   {
      return paths;
   }
}
