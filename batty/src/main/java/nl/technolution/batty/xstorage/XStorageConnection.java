/*
 (C) COPYRIGHT TECHNOLUTION BV, GOUDA NL
| =======          I                   ==          I    =
|    I             I                    I          I
|    I   ===   === I ===  I ===   ===   I  I    I ====  I   ===  I ===
|    I  /   \ I    I/   I I/   I I   I  I  I    I  I    I  I   I I/   I
|    I  ===== I    I    I I    I I   I  I  I    I  I    I  I   I I    I
|    I  \     I    I    I I    I I   I  I  I   /I  \    I  I   I I    I
|    I   ===   === I    I I    I  ===  ===  === I   ==  I   ===  I    I
|                 +---------------------------------------------------+
+----+            |  +++++++++++++++++++++++++++++++++++++++++++++++++|
     |            |             ++++++++++++++++++++++++++++++++++++++|
     +------------+                          +++++++++++++++++++++++++|
                                                        ++++++++++++++|
                                                                 +++++|
 */
package nl.technolution.batty.xstorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.BmsDataJson;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineDataJson;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MachineInfoJson;
import nl.technolution.batty.xstorage.types.MeterInfo;
import nl.technolution.core.Log;

/**
 * Connection to Nissan xStorage device
 */
public class XStorageConnection {

    private static final String GET_MACHINE_INFO_CMD = "GetMachineInfo";
    private static final String GET_MACHINE_DATA_CMD = "GetMachineData";
    private static final String SET_MACHINE_DATA_CMD = "SetMachineData";
    private static final Object GET_BMS_DATA_CMD = "GetBMSData";

    private final Logger log = Log.getLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String address;
    private final String username;
    private final String password;

    private SSLSocketFactory sslSF;

    /**
     * https://172.30.133.212/Dashboard.php -- wifi
     * https://172.30.133.212/Dashboard.php -- technolan
     * 
     * Constructor for {@link XStorageConnection} objects
     *
     * @param address where to find the battery
     */
    public XStorageConnection(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    /**
     * init connection, load certificates
     * 
     * @param trustStorePath
     * @param trustStorePass
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws KeyManagementException
     */
    public void init(String trustStorePath, String trustStorePass) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, FileNotFoundException, IOException, KeyManagementException {

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(trustStorePath), trustStorePass.toCharArray());
       File truststorefile = new File(trustStorePath);
       Preconditions.checkArgument(truststorefile.exists(), "file not found");

       TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
       tmf.init(ks);

       SSLContext sslCtx = SSLContext.getInstance("TLS");
       sslCtx.init(null, tmf.getTrustManagers(), null);
       sslSF = sslCtx.getSocketFactory();
   }

    private HttpsURLConnection getHttpsURLConnection(String url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection)new URL(url).openConnection();
        urlConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        urlConnection.setSSLSocketFactory(sslSF);
        return urlConnection;
    }

    /**
     * @return machine info
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    public MachineInfo getMachineInfo() throws JsonParseException, JsonMappingException, IOException,
            NoSuchAlgorithmException, CertificateException, KeyStoreException, KeyManagementException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_MACHINE_INFO_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            MachineInfoJson result = mapper.readValue(in, MachineInfoJson.class);
            return MachineInfo.fromData(result.getData());
        }
    }

    /**
     * @return machine data
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public MachineData getMachineData() throws JsonParseException, JsonMappingException, IOException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_MACHINE_DATA_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            MachineDataJson result = mapper.readValue(in, MachineDataJson.class);
            System.out.println(mapper.writeValueAsString(result));
            return MachineData.fromData(result.getData());
        }
    }

    /**
     * @return machine data
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public BmsData getBmsData() throws JsonParseException, JsonMappingException, IOException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_BMS_DATA_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            BmsDataJson result = mapper.readValue(in, BmsDataJson.class);
            System.out.println(mapper.writeValueAsString(result));
            return BmsData.fromData(result.getData());
        }
    }

    /**
     * https://172.30.133.212/assets/inc/server.inc.php?action=SetMachineData&D0=batty&D1=batty&D2=2&D3=100
     * 
     * @param percentage
     * @throws IOException
     */
    public void charge(int percentage) throws IOException {
        Preconditions.checkArgument(percentage >= 0 && percentage <= 100);
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=0&D3=%d", address, SET_MACHINE_DATA_CMD, username,
                password, percentage);
        log.debug(url);
        int response = getHttpsURLConnection(url).getResponseCode();
        if (response != 200) {
            log.error("Unexpected response " + response);
        }
    }

    /**
     * 
     * @param percentage
     * @throws IOException
     */
    public void discharge(int percentage) throws IOException {
        Preconditions.checkArgument(percentage >= 0 && percentage <= 100);
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=3&D3=%d", address, SET_MACHINE_DATA_CMD, username,
                password, percentage);
        log.debug(url);
        int response = getHttpsURLConnection(url).getResponseCode();
        if (response != 200) {
            log.error("Unexpected response " + response);
        }
    }

    /**
     * @throws IOException
     */
    public void powerOn() throws IOException {
        // String url = String.format("%s?action=%s&D0=%s&D1=%sD2=1&D3=1", address, SET_MACHINE_DATA_CMD, username,
        // password);
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=2&D3=1", address, SET_MACHINE_DATA_CMD, username,
                password);
        log.debug(url);
        int response = getHttpsURLConnection(url).getResponseCode();
        if (response != 200) {
            log.error("Unexpected response " + response);
        }

    }

    /**
     * @throws IOException
     */
    public void powerOff() throws IOException {
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=0&D3=0", address, SET_MACHINE_DATA_CMD, username,
                password);
        log.debug(url);
        int response = getHttpsURLConnection(url).getResponseCode();
        if (response != 200) {
            log.error("Unexpected response " + response);
        }

    }

    /**
     * 
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public MeterInfo getMeterInfo() throws JsonParseException, JsonMappingException, IOException {
        InputStream src = null;
        MachineInfoJson result = mapper.readValue(src, MachineInfoJson.class);
        String[] data = result.getData();
        return MeterInfo.fromData(data[0]);
    }
}
