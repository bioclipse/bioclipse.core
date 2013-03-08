package net.bioclipse.business;

import static org.junit.Assert.*;

import net.bioclipse.business.BioclipsePlatformManager;

import org.junit.Test;


public class LogfilelocationTest {

    @Test
    public void test() {
        String input = "/Users/xxx/Library/Logs/Bioclipse//bioclipse.log";
        assertEquals( "/Users/xxx/Library/Logs/Bioclipse/bioclipse.log", BioclipsePlatformManager.stripExtraSlashes( input ));
    }

}
