/*
 * $Id:$
 *
 * Copyright (c) 2020 Pegasystems Inc.
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


import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Arrays;


public class TestUtils {
    //private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isFileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }
    public static void writeToFileInUTF8(String filePath, String textToWrite) throws IOException {
        File fileDir = new File(filePath);
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"))) {
            out.append(textToWrite);
        } catch (Exception e) {
        }
    }
    public static void unzip(String zipFilePath, String destinationFolderToUnZip_Path) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(destinationFolderToUnZip_Path);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static  InputStream getResponse(String url) throws MalformedURLException, IOException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(
                "Unable to install all trusting trust manager for accepting all SSL certificates...");
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(180000);
        connection.setReadTimeout(180000);
        connection.setRequestMethod("HEAD");

        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)"
            + " AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.79 Safari/535.11");
        connection.setRequestMethod("GET");
        InputStream is = connection.getInputStream();
        return is;
    }
    public static String getStringFromInputStream(InputStream is) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb.toString();
    }
    public static String getCurrentVersionOfDriverAsString(BufferedReader reader) {
        String line;
        String text = "";
        try {
            while ((line = reader.readLine()) != null) {
                text += "\n" + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       // LOGGER.info("current driver version is " + text.substring(text.indexOf(" "), text.indexOf("(")).trim());
        return text.substring(text.indexOf(" "), text.indexOf("(")).trim();
    }
    public static String getResponseAsString(BufferedReader reader) throws UnsupportedOperationException, IOException {
        String line;
        String text = "";
        while ((line = reader.readLine()) != null) {
            text += "\n" + line;
        }
        return text;
    }
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0
            || OS.indexOf("nux") >= 0
            || OS.indexOf("aix") > 0);
    }
}
