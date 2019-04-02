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
package nl.technolution.fritzy.webrelay;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.technolution.fritzy.io.webrelay.WebRelay;
import nl.technolution.fritzy.io.webrelay.WebRelayState;

/**
 * Test webrelay
 */
public class WebRelayTest {

    private static final int PORT = 80;

    private ExecutorService ee;
    private ServerSocket server;

    private String exprectedServerCommand = "";
    private String returnServerCommand = "";

    private final String webRelayStateTemplate = "<datavalues>\n" +
            "\t<relaystate>%d</relaystate>\n" +
            "\t<inputstate>%d</inputstate>\n" +
            "\t<rebootstate>%d</rebootstate>\n" +
            "\t<totalreboots>%d</totalreboots>\n" +
            "</datavalues>\n";

    @Before
    public void setup() throws IOException {
        // Setup simple server
        server = ServerSocketFactory.getDefault().createServerSocket(PORT);
        ee = Executors.newSingleThreadExecutor();
        ee.execute(() -> {
            try (Socket socket = server.accept();
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream()) {
                ByteBuffer bb = ByteBuffer.allocate(exprectedServerCommand.length());
                byte b;
                do {
                    b = (byte)in.read();
                    bb.put((byte)b);
                } while (bb.position() < exprectedServerCommand.length() && b != -1);
                // Check matching incomming command
                assertEquals(exprectedServerCommand, new String(bb.array(), StandardCharsets.UTF_8));

                // Write expected response
                out.write(returnServerCommand.getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testParsingState() throws IOException {
        exprectedServerCommand = "GET /state.xml HTTP/1.1 \r\n\r\n";

        WebRelayState exp = new WebRelayState(true, true, 0, 0);
        setExpectedReturnState(exp);
        WebRelay relay = new WebRelay(InetAddress.getLocalHost(), PORT);
        WebRelayState act = relay.getState();
        assertEquals(exp, act);
    }

    @Test
    public void testSwitchRelayOn() throws IOException {
        exprectedServerCommand = "GET /state.xml?relayState=1 HTTP/1.1 \r\n\r\n";
        setExpectedReturnState(new WebRelayState(true, true, 0, 0));
        new WebRelay(InetAddress.getLocalHost(), PORT).setRelay(true);
    }

    @Test
    public void testSwitchRelayOff() throws IOException {
        exprectedServerCommand = "GET /state.xml?relayState=0 HTTP/1.1 \r\n\r\n";
        setExpectedReturnState(new WebRelayState(true, true, 0, 0));
        new WebRelay(InetAddress.getLocalHost(), PORT).setRelay(false);
    }

    private void setExpectedReturnState(WebRelayState exp) {
        returnServerCommand = String.format(webRelayStateTemplate, exp.isRelaystate() ? 1 : 0,
                exp.isInputstate() ? 1 : 0, exp.getRebootstate(), exp.getTotalreboots());
    }

    @After
    public void teardown() throws InterruptedException, IOException {
        ee.shutdownNow();
        if (!server.isClosed()) {
            server.close();
        }
    }

}
