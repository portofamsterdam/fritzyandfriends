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
package nl.technolution.fritzy.wallet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersResponseEntry {
    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("password_demo")
    private String password_demo;

    /**
     * Constructor for {@link UsersResponseEntry} objects
     */
    public UsersResponseEntry() {
        // 
    }

    /**
     * Constructor for {@link UsersResponseEntry} objects
     *
     * @param name of user
     * @param email address
     * @param address wallet
     * @param password_demo password
     */
    public UsersResponseEntry(String name, String email, String address, String password_demo) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password_demo = password_demo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword_demo() {
        return password_demo;
    }

    public void setPassword_demo(String password_demo) {
        this.password_demo = password_demo;
    }

}
