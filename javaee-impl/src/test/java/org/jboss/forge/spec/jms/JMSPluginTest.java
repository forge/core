package org.jboss.forge.spec.jms;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.spec.javaee.JMSFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@RunWith(Arquillian.class)
public class JMSPluginTest extends AbstractShellTest
{
    @Test
    public void testSetup() throws Exception
    {
        Project project = initializeJavaProject();
        assertFalse(project.hasFacet(JMSFacet.class));

        queueInputLines("");
        getShell().execute("setup jms");

        assertTrue(project.hasFacet(JMSFacet.class));
    }
}
