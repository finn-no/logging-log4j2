package org.apache.logging.log4j.core.appender;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * 
 * You have to watch the console to see this work, or not.
 * 
 * See:
 * <ul>
 * <li>https://issues.apache.org/jira/browse/LOG4J2-682</li>
 * <li>https://mail-archives.apache.org/mod_mbox/logging-log4j-user/201406.mbox/%3CCAKnbemWoAXryn7UH=qMmwr=ad24La1+qv+
 * cyO9OXxCCCJAGV_g@mail.gmail.com%3E</li>
 * </ul>
 */
public class ProgressConsoleTest {

    private static final Logger LOG = LogManager.getLogger(ProgressConsoleTest.class);

    public static void main(final String[] args) {
        final LoggerContext ctx = Configurator.initialize(ProgressConsoleTest.class.getName(),
                "target/test-classes/log4j2-progress-console.xml");
        // src/test/resources/log4j2-console-progress.xml
        // target/test-classes/log4j2-progress-console.xml
        try {
            for (double i = 0; i <= 1; i = i + 0.05) {
                updateProgress(i);
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } finally {
            Configurator.shutdown(ctx);
        }
    }

    private static void updateProgress(final double progressPercentage) {
        final int width = 50; // progress bar width in chars

        String s = "[";
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {
            s += ".";
        }
        for (; i < width; i++) {
            s += " ";
        }
        s += "](" + (new DecimalFormat("#0.00")).format(progressPercentage * 100) + "%)";
        LOG.info(s);
    }
}
