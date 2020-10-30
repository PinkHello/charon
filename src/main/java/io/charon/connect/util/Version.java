package io.charon.connect.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Version
 *
 * @author HeiShu
 */
public class Version {

    public static final Logger log = LoggerFactory.getLogger(Version.class);

    private static String version = "unknown";

    public static String getVersion() {
        return version;
    }

    static {
        InputStream in = null;
        try {
            Properties props = new Properties();
            in = Version.class.getResourceAsStream(
                    "/kafka-connect-charon-version.properties");
            props.load(in);
            version = props.getProperty("version", version).trim();
            in.close();
        } catch (Exception e) {
            log.warn("Error while loading version:", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("WTF!", e);
                }
            }
        }
    }

}
