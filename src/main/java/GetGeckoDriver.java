import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
public class GetGeckoDriver {
//	private static final Logger LOGGER = LoggerFactory.getLogger(GetGeckoDriver.class.getName());

	public static void getGeckoDriverForFirefox() {
		try {
			String majorVersion = getLocalFirefoxVersion();
		//	LOGGER.info("majorVersion: "+majorVersion);
			String requiredVersion = null;
			String currentVersion = "";
			if (new File("binaries").exists()) {
				try {
					String existingGeckoDriverPath = "binaries" + System.getProperty("file.separator")
							+ "geckodriver.exe";
					if (TestUtils.isFileExists(existingGeckoDriverPath)
							&& TestUtils.isWindows()) {
						//LOGGER.info("yes geckodriver.exe exists");
						currentVersion = TestUtils.getCurrentVersionOfDriverAsString(new BufferedReader(new InputStreamReader(
								Runtime.getRuntime().exec("cmd /c \"" + existingGeckoDriverPath + "\" --version")
										.getInputStream())));
                        TestUtils.writeToFileInUTF8("binaries" + System.getProperty("file.separator")
								+ "do_not_checkin_geckodriverversion.txt", currentVersion);
						//LOGGER.info("current version of firefox " + currentVersion);
					//	LOGGER.info("local Firefox version is " + majorVersion);
					} else {
						//LOGGER.info("geckodriver.exe does not exist or OS is not windows");
					}
				} catch (Exception e) {

				}
			} else {
				new File("binaries").mkdir();
			}
			/***
			 * Need to give latest version for geckodriver.exe
			 **/
			if (Integer.parseInt(majorVersion) >= 60) {
				requiredVersion = "0.30.0";
			} else if (Integer.parseInt(majorVersion) >= 57 && Integer.parseInt(majorVersion) < 60)
				requiredVersion = "0.25.0";
			else if (Integer.parseInt(majorVersion) >= 55 && Integer.parseInt(majorVersion) < 57)
				requiredVersion = "0.20.1";
			else if (Integer.parseInt(majorVersion) >= 53 && Integer.parseInt(majorVersion) < 55)
				requiredVersion = "0.18.0";
			else if (Integer.parseInt(majorVersion) >= 52 && Integer.parseInt(majorVersion) < 53)
				requiredVersion = "0.17.0";

			if (requiredVersion != null) {
				//LOGGER.info("Firefox Version: " + majorVersion + "\tGeckoDriver version needed:" + requiredVersion);
				if (requiredVersion.equals(currentVersion)) {
				//	LOGGER.info("Required geckodriver version is already available. Not attempting to download any new version");
					return;
				} else {
					getGeckoDriver(requiredVersion);
					return;
				}
			}
		} catch (Exception e) {
			//LOGGER.info("Encountered error while checking for gecko driver required for the current firefox version: "+ e.getMessage() + "\n");
			//LOGGER.info("\nUsing the existing geckodriver in binaries folder and proceeding with the test\n");

		}
	}
	private static void getGeckoDriver(String geckoDriverVersion) throws IOException, InterruptedException {
		//LOGGER.info("Downloading the required gecko driver");
		if (TestUtils.isWindows()) {
			downloadGeckoDriver("https://github.com/mozilla/geckodriver/releases/download/v" + geckoDriverVersion
					+ "/geckodriver-v" + geckoDriverVersion + "-win64.zip");
			if (new File("binaries" + System.getProperty("file.separator") + "geckodriver.exe").isFile()) {
			//	LOGGER.info("geckodriver is downloaded ");
			}
		} else if (TestUtils.isMac()) {
			downloadGeckoDriver("https://github.com/mozilla/geckodriver/releases/download/v" + geckoDriverVersion
					+ "/geckodriver-v" + geckoDriverVersion + "-macos.tar.gz");
			if (new File("binaries" + System.getProperty("file.separator") + "geckodriver_mac64").isFile()) {
			//	LOGGER.info("geckodriver is downloaded ");
			}
		} else if (TestUtils.isUnix()) {
			downloadGeckoDriver("https://github.com/mozilla/geckodriver/releases/download/v" + geckoDriverVersion
					+ "/geckodriver-v" + geckoDriverVersion + "-linux32.tar.gz");
			if (new File("binaries" + System.getProperty("file.separator") + "geckodriver").isFile()) {
			//	LOGGER.info("geckodriver is downloaded ");
			}
		}
        TestUtils.writeToFileInUTF8(
            "binaries" + System.getProperty("file.separator") + "do_not_checkin_geckodriverversion.txt",
            geckoDriverVersion);
	}

	private static void downloadGeckoDriver(String url)
			throws FileNotFoundException,IOException, InterruptedException {
		InputStream is = TestUtils.getResponse(url);
		String filePath;
		if (TestUtils.isWindows()) {
			filePath = "binaries" + System.getProperty("file.separator") + "geckodriver.zip";
		} else
			filePath = "binaries" + System.getProperty("file.separator") + "geckodriver.tar.gz";
		int inByte;
		try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
			while ((inByte = is.read()) != -1)
				fos.write(inByte);
		} catch (Exception e) {
			System.out.println("Exception is " + e);
		} finally {
			is.close();
		}
		if (TestUtils.isWindows()) {
            TestUtils.unzip("binaries" + System.getProperty("file.separator") + "geckodriver.zip", "binaries");
			new File(filePath).delete();
		} else {

			String destFolder = "binaries";
			Runtime.getRuntime().exec("tar -C " + destFolder + " -xvf " + filePath);
			Thread.sleep(3000);
			if (new File("binaries" + System.getProperty("file.separator") + "geckodriver").isFile()) {
				//LOGGER.info("geckodriver is downloaded ");
				if (new File(filePath).isFile()) {
					new File(filePath).delete();
				//	LOGGER.info("geckodriver.tar.gz is deleted ");
				}
			}
		}
	}
	private static String getLocalFirefoxVersion() throws Exception, IOException {
		String output = null;
		String majorVersion = null;

		if (TestUtils.isWindows()) {
			String firefoxBrowserPath = "";
			String userDir = System.getProperty("user.home").split("\\\\")[2];
			if (TestUtils.isFileExists("C:\\Program Files\\Mozilla Firefox\\firefox.exe")) {
				firefoxBrowserPath = "C:\\\\Program Files\\\\Mozilla Firefox\\\\firefox.exe";
			} else {
				throw new Exception(
						"Couldn't file path for firefox.exe, using the existing geckodriver to launch firefox using selenium");
			}
			output = TestUtils.getResponseAsString(new BufferedReader(new InputStreamReader(Runtime.getRuntime()
					.exec("cmd /c wmic datafile where name=\"" + firefoxBrowserPath + "\" get Version /value")
					.getInputStream())));
			majorVersion = output.replaceAll("\\\n", "").replace("Version=", "").split("\\.")[0];

		} else if (TestUtils.isMac()) {
		    String commandStrArr[]= {"/Applications/Firefox.app/Contents/MacOS/firefox","--version"};
			String macOutput = TestUtils.getResponseAsString(new BufferedReader(new InputStreamReader(Runtime.getRuntime()
                     .exec(commandStrArr)
					.getInputStream())));
			majorVersion = macOutput.replaceAll("\\\n", "").replace("firefox", "").split("\\.")[0];
            majorVersion= majorVersion.substring(majorVersion.lastIndexOf(" ") + 1).trim();
		} else if (TestUtils.isUnix()) {
			String linuxOutput = TestUtils.getResponseAsString(new BufferedReader(
					new InputStreamReader(Runtime.getRuntime().exec("firefox -version").getInputStream())));
			majorVersion = linuxOutput.replaceAll("\\\n", "").replace("Mozilla Firefox ", "").split("\\.")[0];
		}
		//LOGGER.info("######Version is : " + majorVersion);
		return majorVersion;
	}

	private static String getGeckoDriverVersionmin(String firefoxVersion) {
		Map<String, String> geckoDriverVersionsmin = new HashMap<String, String>();
		geckoDriverVersionsmin.put("52", "0.17.0");
		geckoDriverVersionsmin.put("53", "0.18.0");
		geckoDriverVersionsmin.put("55", "0.20.1");
		geckoDriverVersionsmin.put("57", "0.25.0");
		geckoDriverVersionsmin.put("60", "0.26.0");
		return geckoDriverVersionsmin.get(firefoxVersion);
	}

	private static String getGeckoDriverVersionMax(String firefoxVersion) {
		Map<String, String> geckoDriverVersionsmax = new HashMap<String, String>();
		geckoDriverVersionsmax.put("79", "0.24.0");
		geckoDriverVersionsmax.put("62", "0.20.1");
		return geckoDriverVersionsmax.get(firefoxVersion);
	}

}
