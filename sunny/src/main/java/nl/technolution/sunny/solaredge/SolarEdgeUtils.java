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
package nl.technolution.sunny.solaredge;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.technolution.sunny.solaredge.sunspec.UnsignedLong;

/**
 * Common functions for SolarEdge API
 */
public final class SolarEdgeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SolarEdgeUtils.class);

    /**
     * @param bytes
     * @return
     */
    public static float registersToModiconFloat(byte[] bytes) {
        byte[] singleFloat = new byte[4];
        singleFloat[2] = bytes[0];
        singleFloat[3] = bytes[1];
        singleFloat[0] = bytes[2];
        singleFloat[1] = bytes[3];
        return ByteBuffer.wrap(singleFloat).asFloatBuffer().get();
    }

    /**
     * @param f
     * @return
     */
    public static byte[] modiconFloatToRegisters(float f) {
        byte[] value = new byte[4];
        ByteBuffer.wrap(value).asFloatBuffer().put(f);
        return new byte[] { value[2], value[3], value[0], value[1] };
    }

    /**
     * @param bytes
     * @return
     */
    public static int registersToSolarEdgeInteger(byte[] bytes) {
        byte[] singleInteger = new byte[4];
        singleInteger[2] = bytes[0];
        singleInteger[3] = bytes[1];
        singleInteger[0] = bytes[2];
        singleInteger[1] = bytes[3];
        return ByteBuffer.wrap(singleInteger).asIntBuffer().get();
    }

    /**
     * @param i
     * @return
     */
    public static byte[] solarEdgeIntegerToRegisters(int i) {
        byte[] value = new byte[4];
        ByteBuffer.wrap(value).asIntBuffer().put(i);
        return new byte[] { value[2], value[3], value[0], value[1] };
    }

    /**
     * @param bytes
     * @return
     */
    public static long registersToSolarEdgeLong(byte[] bytes) {
        byte[] orderedBytes = new byte[8];
        orderedBytes[6] = bytes[0];
        orderedBytes[7] = bytes[1];
        orderedBytes[4] = bytes[2];
        orderedBytes[5] = bytes[3];
        orderedBytes[2] = bytes[4];
        orderedBytes[3] = bytes[5];
        orderedBytes[0] = bytes[6];
        orderedBytes[1] = bytes[7];
        return new BigInteger(orderedBytes).longValue();
    }

    /**
     * @param bytes
     * @return
     */
    public static UnsignedLong registersToSolarEdgeUnsignedLong(byte[] bytes) {
        byte[] orderedBytes = new byte[8];
        orderedBytes[6] = bytes[0];
        orderedBytes[7] = bytes[1];
        orderedBytes[4] = bytes[2];
        orderedBytes[5] = bytes[3];
        orderedBytes[2] = bytes[4];
        orderedBytes[3] = bytes[5];
        orderedBytes[0] = bytes[6];
        orderedBytes[1] = bytes[7];
        return new UnsignedLong(new BigInteger(orderedBytes));
    }

    /**
     * @param l
     * @return
     */
    public static byte[] solarEdgeLongToRegisters(long l) {
        byte[] value = new byte[8];
        ByteBuffer.wrap(value).asLongBuffer().put(l);
        return new byte[] { value[6], value[7], value[4], value[5], value[2], value[3], value[0], value[1] };
    }

    /**
     * @param ul
     * @return
     */
    public static byte[] solarEdgeUnsignedLongToRegisters(UnsignedLong ul) {
        byte[] signedValueBE = ul.getValue().toByteArray();
        LOG.debug("toByteArray result: " + byteArrayToHex(signedValueBE));
        byte[] value = new byte[8];
        int copyOffset = 0;
        // NOTE: An extra byte for the sign bit can be added by BigInteger.toByteArray, as we need a 64 bit unsigned
        // value remove it by shifting the bytes 1 byte position during copy to value array
        if (signedValueBE[0] == 0) {
            copyOffset = 1;
        }
        // signedValueBE contains no leading zero's, copy bytes to value buffer filling out at the right
        int bytesInSrc = signedValueBE.length - copyOffset;
        for (int i = 0; i < (bytesInSrc); i++) {
            value[8 - bytesInSrc + i] = signedValueBE[i + copyOffset];
        }
        LOG.debug("copy to val result: " + byteArrayToHex(value));
        return new byte[] { value[6], value[7], value[4], value[5], value[2], value[3], value[0], value[1] };
    }

    /**
     * @param registers
     * @return
     */
    public static byte[] registersToBytes(Register[] registers) {
        byte[] bytes = new byte[0];
        for (Register r : registers) {
            bytes = ArrayUtils.addAll(bytes, r.toBytes());
        }
        return bytes;
    }

    /**
     * @param bytes
     * @return
     */
    public static Register[] bytesToRegisters(byte[] bytes) {
        Register[] registers = new Register[bytes.length / 2];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new SimpleRegister(bytes[i * 2], bytes[i * 2 + 1]);
        }
        return registers;
    }

    /**
     * @param bytes
     * @return
     */
    public static String registersToString(byte[] bytes) {
        return new String(bytes, Charset.forName("US-ASCII"));
    }

    /**
     * @param unitId
     * @return
     */
    public static WriteMultipleRegistersRequest createWriteRequest(int unitId) {
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest();
        request.setUnitID(unitId);
        return request;
    }

    /**
     * 
     */
    public static ReadMultipleRegistersRequest createReadRequest(int unitId) {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest();
        request.setUnitID(unitId);
        return request;
    }

    /**
     * @param connection
     * @return
     */
    public static ModbusTransaction createTransaction(TCPMasterConnection connection) {
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        // don't set the setReconnecting flag, it gives more problems than it solves...
        transaction.setReconnecting(false);
        // by default use 2 retries, this seems to decrease the number of transaction failures
        int retries = 2;
        transaction.setRetries(retries);
        // MRV-646: try if setting transaction id and checking them solves the 'reqquest and responce are out of sync'
        // problem.
        transaction.setCheckingValidity(true);
        LOG.debug("Transaction created, reconnect = " + transaction.isReconnecting() + " retries = " + retries +
                " validity check = " + transaction.isCheckingValidity());
        return transaction;
    }

    /**
     * @param a
     * @return
     */
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(255);
        sb.append("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(String.format("%02X", a[i]));
            if (i < (a.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
