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

/**
 * Contract address
 */
public enum EContractAddress {
    KWH("kwh"),
    ETH("eth"),
    EUR("eur");

    private final String contractName;

    /**
     * Constructor for {@link EContractAddress} objects
     *
     * @param contractName to set
     */
    EContractAddress(String contractName) {
        this.contractName = contractName.toLowerCase();
    }

    public String getContractName() {
        return contractName;
    }

    /**
     * Get EContractAddress for gioven contract name
     * 
     * @param contractName
     * @return
     */
    public static EContractAddress getByContractName(String contractName) {
        for (EContractAddress addr : EContractAddress.values()) {
            if (addr.contractName.equals(contractName.toLowerCase())) {
                return addr;
            }
        }
        throw new IllegalArgumentException("Unknown contract" + contractName);
    }
}
