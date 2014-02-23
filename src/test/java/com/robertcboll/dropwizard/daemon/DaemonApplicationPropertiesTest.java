package com.robertcboll.dropwizard.daemon;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests the parsing of properties in {@link com.robertcboll.dropwizard.daemon.DaemonApplication}.
 *
 */
public class DaemonApplicationPropertiesTest {

    private static DaemonApplication app;
    private static Properties props;

    @BeforeClass
    public static void setupAppAndProps() {
        app = new DaemonApplication() {

            @Override
            public void initialize(Bootstrap bootstrap) {
                //noop
            }

            @Override
            public void run(Configuration configuration, Environment environment) throws Exception {
                //noop
            }
        };
        props = new Properties();
    }

    @Test
    public void testDefaultPidfile() {
        assertEquals("non-existing pidfile property -> default",
                DaemonApplication.DEFAULT_PIDFILE, app.getPidfileOrDefault(props));

        props.setProperty(DaemonApplication.PROPERTY_PIDFILE, "");
        assertEquals("empty string pidfile property -> default",
                DaemonApplication.DEFAULT_PIDFILE, app.getPidfileOrDefault(props));

        props.remove(DaemonApplication.PROPERTY_PIDFILE);
    }

    @Test
    public void testCustomPidfile() {
        props.setProperty(DaemonApplication.PROPERTY_PIDFILE, "./app.pid");
        assertEquals("populated pidfile -> correct",
                "./app.pid", app.getPidfileOrDefault(props));

        props.remove(DaemonApplication.PROPERTY_PIDFILE);
    }

    @Test
    public void testWillDaemonize() {
        props.setProperty(DaemonApplication.PROPERTY_DAEMON, "true");
        assertTrue("true daemon property -> true", app.shouldDaemonize(props));

        props.remove(DaemonApplication.PROPERTY_DAEMON);
    }

    @Test
    public void testWontDaemonize() {
        assertFalse("non-existing daemon property -> false", app.shouldDaemonize(props));

        props.setProperty(DaemonApplication.PROPERTY_DAEMON, "");
        assertFalse("empty daemon property -> false", app.shouldDaemonize(props));

        props.setProperty(DaemonApplication.PROPERTY_DAEMON, "false");
        assertFalse("false daemon property -> false", app.shouldDaemonize(props));

        props.setProperty(DaemonApplication.PROPERTY_DAEMON, "asdf");
        assertFalse("random daemon property -> false", app.shouldDaemonize(props));
    }
}
