package com.robertcboll.dropwizard.daemon;

import com.google.common.io.Resources;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the accompanying script {@code scripts/test-non-blocking.sh} executes without blocking. A timer
 * is used to determine if the script blocks, so this is particularly susceptible to false negatives. It should,
 * however, be relatively safe from false positives.
 */
public class DaemonApplicationIntegrationTest {

    private final static Logger log = LoggerFactory.getLogger(DaemonApplicationIntegrationTest.class);

    private final static Runtime RUN = Runtime.getRuntime();

    @Test
    public void testNonBlocking() throws Exception {
        assertFalse("the thread blocks for shorter than 5 seconds",
                checkIfBlocks(getScript("scripts/test-non-blocking.sh"), 5000, 100));
    }

    @Test
    public void testBlocking() throws Exception {
        assertTrue("the thread blocks for longer than 5 seconds",
                checkIfBlocks(getScript("scripts/test-blocking.sh"), 5000, 100));
    }

    /**
     * Returns true if the given {@code command} blocks for longer than {@code timeout}. Uses {@code interval} as the
     * loop wait interval.
     * @param command the command to execute
     * @param timeout the max timeout to wait for execution completion
     * @param interval the loop wait interval
     * @return true if the process blocks longer than timeout, false otherwise
     * @throws Exception for unexpected errors
     */
    private static boolean checkIfBlocks(final String command, final long timeout, final long interval) throws Exception {
        final Process proc = RUN.exec(command);
        final Thread waiter = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    proc.waitFor();
                } catch (final InterruptedException e) {
                    //ignore
                }
            }
        });
        waiter.start();

        long endtime = System.currentTimeMillis() + timeout;
        do {
            if (waiter.isAlive()) Thread.sleep(interval);
            else {
                cleanup(proc, waiter);
                return false;
            }
        } while (System.currentTimeMillis() < endtime);

        cleanup(proc, waiter);
        return true;
    }

    private String getScript(final String path) {
        final File script = new File(Resources.getResource(path).getFile());
        assertTrue(script.setExecutable(true));
        return script.getAbsolutePath();
    }

    private static void cleanup(final Process proc, final Thread waiter) {
        proc.destroy();
        waiter.interrupt();
    }
}
