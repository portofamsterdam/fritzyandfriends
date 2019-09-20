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
package nl.technolution.batty.xstorage.connection;

import java.io.File;
import java.io.FileInputStream;
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

import nl.technolution.Log;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.BmsDataJson;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineDataJson;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MachineInfoJson;
import nl.technolution.batty.xstorage.types.MeterInfo;

/**
 * https://172.30.133.212/Dashboard.php -- wifi https://172.30.133.212/Dashboard.php -- technolan
 * 
 * Connection to Nissan xStorage device
 */
class XStorageConnection implements IXStorageConnection {

    private static final String GET_MACHINE_INFO_CMD = "GetMachineInfo";
    private static final String GET_MACHINE_DATA_CMD = "GetMachineData";
    private static final String SET_MACHINE_DATA_CMD = "SetMachineData";
    private static final String GET_BMS_DATA_CMD = "GetBMSData";

    private final Logger log = Log.getLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    private String address;
    private String username;
    private String password;
    private SSLSocketFactory sslSF;

    /**
     * @param config init connection
     */
    void init(BattyConfig config) {
        File truststorefile = new File(config.getTruststore());
        Preconditions.checkArgument(truststorefile.exists(), "truststore not found in: " + config.getTruststore());

        this.address = config.getHost();
        this.username = config.getUsername();
        this.password = config.getPassword();

        try (InputStream inFile = new FileInputStream(truststorefile)) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(inFile, config.getTruststorepass().toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, tmf.getTrustManagers(), null);
            sslSF = sslCtx.getSocketFactory();
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException | CertificateException | 
                KeyStoreException ex) {
            log.error("Unable to load certificate", ex);
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * @return machine info
     */
    @Override
    public MachineInfo getMachineInfo() throws XStorageException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_MACHINE_INFO_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            MachineInfoJson result = mapper.readValue(in, MachineInfoJson.class);
            return MachineInfo.fromData(result.getData());
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }
    }

    /**
     * @return machine data
     */
    @Override
    public MachineData getMachineData() throws XStorageException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_MACHINE_DATA_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            MachineDataJson result = mapper.readValue(in, MachineDataJson.class);
            return MachineData.fromData(result.getData());
        } catch (XStorageException | IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }
    }

    /**
     * @return machine data
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Override
    public BmsData getBmsData() throws XStorageException {
        String url = String.format("%s?action=%s&D0=%s&D1=%s", address, GET_BMS_DATA_CMD, username, password);
        log.debug(url);
        try (InputStream in = getHttpsURLConnection(url).getInputStream()) {
            BmsDataJson result = mapper.readValue(in, BmsDataJson.class);
            return BmsData.fromData(result.getData());
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }
    }

    /**
     * https://172.30.133.212/assets/inc/server.inc.php?action=SetMachineData&D0=batty&D1=batty&D2=2&D3=100
     * 
     * @param percentage
     * @throws IOException
     */
    @Override
    public void charge(int percentage) throws XStorageException {
        Preconditions.checkArgument(percentage >= 0 && percentage <= 100);
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=2&D3=%d", address, SET_MACHINE_DATA_CMD, username,
                password, percentage);
        log.debug(url);
        try {
            int response = getHttpsURLConnection(url).getResponseCode();
            if (response != 200) {
                log.error("Unexpected response {}", response);
            }
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }

    }

    /**
     * 
     * @param percentage
     * @throws IOException
     */
    @Override
    public void discharge(int percentage) throws XStorageException {
        Preconditions.checkArgument(percentage >= 0 && percentage <= 100);
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=3&D3=%d", address, SET_MACHINE_DATA_CMD, username,
                password, percentage);
        log.debug(url);
        try {
            int response = getHttpsURLConnection(url).getResponseCode();
            if (response != 200) {
                log.error("Unexpected response {}", response);
            }
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }
    }

    /**
     * @throws IOException
     */
    @Override
    public void powerOn() throws XStorageException {
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=1&D3=1", address, SET_MACHINE_DATA_CMD, username,
                password);
        log.debug(url);
        try {
            int response = getHttpsURLConnection(url).getResponseCode();
            if (response != 200) {
                log.error("Unexpected response {}", response);
            }
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }

    }

    /**
     * @throws IOException
     */
    @Override
    public void powerOff() throws XStorageException {
        String url = String.format("%s?action=%s&D0=%s&D1=%sD2=0&D3=0", address, SET_MACHINE_DATA_CMD, username,
                password);
        log.debug(url);
        try {
            int response = getHttpsURLConnection(url).getResponseCode();
            if (response != 200) {
                log.error("Unexpected response {}", response);
            }
        } catch (IOException e) {
            throw new XStorageException(e.getMessage(), e);
        }

    }

    /**
     * 
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Override
    public MeterInfo getMeterInfo() throws XStorageException {
        InputStream src = null;
        MachineInfoJson result;
        try {
            result = mapper.readValue(src, MachineInfoJson.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        String[] data = result.getData();
        return MeterInfo.fromData(data[0]);
    }


    private HttpsURLConnection getHttpsURLConnection(String url) throws IOException {
        Preconditions.checkNotNull(sslSF, "init first");
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
}
