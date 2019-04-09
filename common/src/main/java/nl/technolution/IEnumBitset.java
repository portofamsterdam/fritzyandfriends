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
package nl.technolution;

import java.util.EnumSet;
import java.util.Set;

/**
 * Retrieves a unique bit mask for an Enum value. Each enum value should be given a unique bit number for a position in
 * the mask. 
 */
public interface IEnumBitset {

    /**
     * returns the bit mask of the enum value MUST BE UNIQUE!!
     *
     * @return bit mask
     */
    long getMask();

    /**
     * Convert binary representation of a Enum that implements {@link IEnumBitset} back to an EnumSet of that Enum.
     * the Enum must implement IEnumSetBitmask and all Enum values must be given unique bit numbers.
     *
     * @param <T> Enum type implementing {@link IEnumBitset}.
     * @param bitmask to get Enum for
     * @param clazz Enum to get bitmask for (must implement {@link IEnumBitset})
     * @return EnumSet of given Enum type class with values corresponding to bitmask.
     */
    static <T extends Enum<T> & IEnumBitset> EnumSet<T> getEnumSet(long bitmask, Class<T> clazz) {
        EnumSet<T> set = EnumSet.noneOf(clazz);
    
        for (T value : clazz.getEnumConstants()) {
            if ((value.getMask() & bitmask) != 0) {
                set.add(value);
            }
        }
        return set;
    }

    /**
     * Create a bitmask from an EnumSet to store in db. use EnumUtils.getEnumSet(bitmask, Class&lt;T&gt; clazz) to
     * convert back to EnumSet. Enum must implement {@link IEnumBitset} and all enum values must be given unique bit
     * numbers.
     *
     * @param <T> Enum type implementing {@link IEnumBitset}.
     * @param set to convert
     * @return binary representation of enum
     */
    static <T extends Enum<T> & IEnumBitset> long getMask(Set<T> set) {
        long binary = 0L;
        if (set != null) {
            for (T status : set) {
                binary |= status.getMask();
            }
        }
        return binary;
    }

    /**
     * Create a bitmask from an EnumSet to store in db. use EnumUtils.getEnumSet(bitmask, Class&lt;T&gt; clazz) to
     * convert back to EnumSet. Enum must implement {@link IEnumBitset} and all enum values must be given unique bit
     * numbers.
     *
     * @param <T> Enum type implementing {@link IEnumBitset}.
     * @param set to convert
     * @return binary representation of enum
     */
    static <T extends Enum<T> & IEnumBitset> long getMask(T... set) {
        long binary = 0L;
        for (T status : set) {
            binary |= status.getMask();
        }
        return binary;
    }
}
