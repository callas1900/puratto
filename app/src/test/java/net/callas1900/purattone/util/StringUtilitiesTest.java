package net.callas1900.purattone.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ryo on 12/2/16.
 */
public class StringUtilitiesTest {
    @Test
    public void isEmpty() throws Exception {
        assertTrue(StringUtilities.isEmpty(null));
        assertTrue(StringUtilities.isEmpty(""));
        assertFalse(StringUtilities.isEmpty("a"));
    }

}