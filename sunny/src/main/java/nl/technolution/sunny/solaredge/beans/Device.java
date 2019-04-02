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

import java.util.ArrayList;
import java.util.List;

/**
 * Class defines device for RPC request/response
 * 
 * @param <C> Kind of channel type string or Channel
 */
public final class Device<C> {
    private String key;
    private String name;
    private List<C> channels = new ArrayList<C>();
    private List<Device<C>> children = new ArrayList<Device<C>>();
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<C> getChannels() {
        return channels;
    }        
    
    public List<Device<C>> getChildren() {
        return children;
    }
    
    public void setChildren(List<Device<C>> children) {
        this.children = children;
    }
}
