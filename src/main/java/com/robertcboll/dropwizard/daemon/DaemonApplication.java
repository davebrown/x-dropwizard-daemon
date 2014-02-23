package com.robertcboll.dropwizard.daemon;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.sun.akuma.Daemon;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Daemonizes the application, if the {@code PROPERTY_DAEMON} is set to {@code true}.
 */
public abstract class DaemonApplication<T extends Configuration> extends Application<T> {

    public static final Logger log = LoggerFactory.getLogger(DaemonApplication.class);

    @VisibleForTesting static final String PROPERTY_DAEMON = "daemon";
    @VisibleForTesting static final String PROPERTY_PIDFILE = "pidfile";
    @VisibleForTesting static final String DEFAULT_PIDFILE = "/var/run/daemon.pid";

    /**
     * Obtains the pidfile property, defaulting to {@code DEFAULT_PIDFILE} if not set. Delegates to
     * {@code daemonize(pidfilePath)}.
     *
     * @return this DaemonApplication
     * @throws Exception if daemonization fails
     */
    protected DaemonApplication daemonize() throws Exception {
        return daemonize(getPidfileOrDefault(System.getProperties()));
    }

    /**
     * Performs daemonization on the current process, if not done already. Writes pid to {@code pidfilePath} and
     * sets up {@code deleteOnExit} on that file.
     * @param pidfilePath the path of the file to hold the {@code pid}.
     *
     * @return this DaemonApplication
     * @throws Exception if daemonization fails
     */
    protected DaemonApplication daemonize(final String pidfilePath) throws Exception {
        final Daemon daemon = new Daemon();
        if (daemon.isDaemonized()) {
            final File pidfile = new File(pidfilePath);
            daemon.init(pidfile.getAbsolutePath());

            if (pidfile.exists()) {
                pidfile.deleteOnExit();
            } else {
                log.warn("Unable to set automatic deletion of pidfile {}.", pidfile.getAbsolutePath());
            }
        } else if (shouldDaemonize(System.getProperties())) {
            daemon.daemonize();
            System.out.println("Launched daemon into background.");
            System.exit(0);
        }
        return this;
    }

    @VisibleForTesting String getPidfileOrDefault(final Properties props) {
        return Optional.fromNullable(Strings.emptyToNull(props.getProperty(PROPERTY_PIDFILE))).or(DEFAULT_PIDFILE);
    }

    @VisibleForTesting boolean shouldDaemonize(final Properties props) {
        return Boolean.parseBoolean(props.getProperty(PROPERTY_DAEMON));
    }
}
