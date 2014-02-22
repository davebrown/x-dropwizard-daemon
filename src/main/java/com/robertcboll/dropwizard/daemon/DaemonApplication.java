package com.robertcboll.dropwizard.daemon;

import com.sun.akuma.Daemon;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Daemonizes the service, if the {@code PROPERTY_DAEMON} is set to {@code true}.
 */
public abstract class DaemonApplication<T extends Configuration> extends Application<T> {

    public static final Logger log = LoggerFactory.getLogger(DaemonApplication.class);

    protected final static String PROPERTY_DAEMON = "daemon";

    protected static final String PROPERTY_PIDFILE = "pidfile";

    protected DaemonApplication daemonize() throws Exception {
        final String pidfile = System.getProperty(PROPERTY_PIDFILE);
        if (pidfile != null && !"".equals(pidfile)) {
            return daemonize(pidfile);
        } else {
            log.warn("Not daemonizing; missing {} system property.", PROPERTY_PIDFILE);
            return this;
        }
    }

    protected DaemonApplication daemonize(final String pidfilePath) throws Exception {
        Daemon daemon = new Daemon();

        if (daemon.isDaemonized()) {
            File pidfile = new File(pidfilePath);

            daemon.init(pidfile.getAbsolutePath());

            if (pidfile.exists()) {
                pidfile.deleteOnExit();
                log.debug("Setting {} to delete on exit.", pidfile.getAbsolutePath());
            }
        } else {
            if (shouldDaemonize()) {
                daemon.daemonize();
                System.exit(0);
            }
        }
        return this;
    }

    protected boolean shouldDaemonize() {
        return Boolean.parseBoolean(System.getProperty(PROPERTY_DAEMON));
    }
}
