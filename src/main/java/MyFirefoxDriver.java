/*
 *
 * Copyright (c) 2019 Pegasystems Inc.
 * All rights reserved.
 *
 * This  software  has  been  provided pursuant  to  a  License
 * Agreement  containing  restrictions on  its  use.   The  software
 * contains  valuable  trade secrets and proprietary information  of
 * Pegasystems Inc and is protected by  federal   copyright law.  It
 * may  not be copied,  modified,  translated or distributed in  any
 * form or medium,  disclosed to third parties or used in any manner
 * not provided for in  said  License Agreement except with  written
 * authorization from Pegasystems Inc.
*/

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * To get the Firefox driver
 * @author Tejesh kumar reddy Nagireddy
 * @since 19-May-2021
 *
 */
public class MyFirefoxDriver {

	//private static final Logger LOGGER = LoggerFactory.getLogger(PegaFirefoxDriver.class);
    private WebDriver driver;
    String firefoxDriverWindowsPath = "binaries" + File.separator + "geckodriver.exe";
    String firefoxDriverLinuxPath = "binaries" + File.separator + "geckodriver";

	public WebDriver getFirefoxDriver() {
	    GetGeckoDriver.getGeckoDriverForFirefox();
		if (TestUtils.isWindows()) {
			System.setProperty("webdriver.gecko.driver",  firefoxDriverWindowsPath);
		} else {
				//LOGGER.debug("Setting the linux chrome driver path to:" +  firefoxDriverLinuxPath);
				File f = new File( firefoxDriverLinuxPath);
				f.setExecutable(true);
				System.setProperty("webdriver.gecko.driver", firefoxDriverLinuxPath);
		}
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
        return driver;
	}


}
