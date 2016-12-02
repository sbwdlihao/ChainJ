package com.lihao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Created by sbwdlihao on 02/12/2016.
 */
public class LoggerTest {

    @Test
    public void TestConfiguration() {
        Logger logger = LogManager.getLogger();

        logger.info("this is a info message");
        logger.error("this is an error message");

    }
}
