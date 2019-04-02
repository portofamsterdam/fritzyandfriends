/*
 (C) COPYRIGHT 2015 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredge.beans;

/**
 * Marker interface for RPC Request classes
 */
public interface IRpcRequest {
    /** 
     * @return Version of RPC
     */
    String getVersion();    
    
    /** 
     * @param version
     *      Version of RPC
     */
    void setVersion(String version);
    
    /** 
     * @return ID of RPC request
     */
    String getId();

    /**
     * 
     * @param id 
     *       ID of RPC request
     */
    void setId(String id);

    /**
     * @return Format of RPC request
     */
    String getFormat();

    /**
     * @param format
     *      Format of RPC request
     */
    void setFormat(String format);
    
    /**
     * @return Procedure call of RPC request
     */
    String getProc();
}
