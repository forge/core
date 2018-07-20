package org.jboss.forge.addon.database.tools.generate;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class EntityGeneratorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Flyway flyway = new Flyway();

    @Before
    public void setUp() {
        flyway.setDataSource(getConnectionUrl(), getUsername(), getPassword());
        flyway.migrate();
    }

    @After
    public void tearDown() {
        flyway.clean();
    }

    @Test
    public void should_generate_entity() throws Exception {
        GenerateEntitiesCommandDescriptor descriptor = new GenerateEntitiesCommandDescriptor();
        ConnectionProfile connectionProfile = new ConnectionProfile();
        connectionProfile.setDialect(getDialect());
        connectionProfile.setUrl(getConnectionUrl());
        connectionProfile.setUser(getUsername());
        connectionProfile.setPassword(getPassword());
        descriptor.setUrls(new URL[]{Driver.class.getProtectionDomain().getCodeSource().getLocation()});
        descriptor.setDriverClass(getDriverClass());
        descriptor.setConnectionProfile(connectionProfile);
        descriptor.setTargetPackage("entities");

        String schema = getSchema();
        String catalog = getCatalog();
        List<String> tables = Arrays.asList("PERSON");

        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", getDriverClass());
        properties.setProperty("hibernate.connection.username", getUsername());
        properties.setProperty("hibernate.connection.password", getPassword());
        properties.setProperty("hibernate.connection.url", getConnectionUrl());
        properties.setProperty("hibernate.dialect", getDialect());
        descriptor.setConnectionProperties(properties);


        EntityGenerator generator = new EntityGenerator();
        File sourceFolder = temporaryFolder.getRoot();
        Collection<String> entities = generator.exportSelectedEntities(sourceFolder, descriptor, catalog, schema, tables);
        assertThat(new File(sourceFolder, "entities/Person.java")).exists();
    }

    private String getConnectionUrl() {
        return "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    }

    private String getDriverClass() {
        return "org.h2.Driver";
    }

    private String getUsername() {
        return "sa";
    }

    private String getPassword() {
        return "sa";
    }

    private String getSchema() {
        return "PUBLIC";
    }

    private String getCatalog() {
        return "TEST";
    }

    private String getDialect() {
        return "org.hibernate.dialect.H2Dialect";
    }

}
