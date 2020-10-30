package io.charon.connect.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class VersionTest {

    @Test
    public void getVersion() {
        Assert.assertEquals("1.0.0", Version.getVersion());
    }
}