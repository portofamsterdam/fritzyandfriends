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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents State object from the relay device. 
 */
public class WebRelayState {

    private static final Pattern RELAYSTATEPATTERN = Pattern.compile("<relaystate>(\\d+)", Pattern.MULTILINE);
    private static final Pattern INPUTSTATEPATTERN = Pattern.compile("<inputstate>(\\d+)", Pattern.MULTILINE);
    private static final Pattern REBOOTSTATEPATTERN = Pattern.compile("<rebootstate>(\\d+)", Pattern.MULTILINE);
    private static final Pattern TOTALREBOOTSPATTERN = Pattern.compile("<totalreboots>(\\d+)", Pattern.MULTILINE);

    private final boolean relaystate;
    private final boolean inputstate;
    private final int rebootstate;
    private final int totalreboots;

    /**
     * Constructor for {@link WebRelayState} objects
     *
     * @param relaystate coil energized
     * @param inputstate voltage applied to input
     * @param rebootstate 0=Auto Reboot off, 1=Pinging, 2=Waiting for response, 3=Rebooting, 4=Waiting for boot
     * @param totalreboots the umber of times WebRelay has automatically rebooted the device that it is controlling.
     */
    public WebRelayState(boolean relaystate, boolean inputstate, int rebootstate, int totalreboots) {
        this.relaystate = relaystate;
        this.inputstate = inputstate;
        this.rebootstate = rebootstate;
        this.totalreboots = totalreboots;
    }

    public boolean isRelaystate() {
        return relaystate;
    }

    public boolean isInputstate() {
        return inputstate;
    }

    public int getRebootstate() {
        return rebootstate;
    }

    public int getTotalreboots() {
        return totalreboots;
    }

    /**
     * Parse XML of state
     * 
     * @param input XML
     * @return instance
     */
    public static WebRelayState parse(String input) {
        // Not the cleanest way to read the XML but easiest
        return new WebRelayState(getValueFromXml(RELAYSTATEPATTERN, input) == 1,
                getValueFromXml(INPUTSTATEPATTERN, input) == 1, getValueFromXml(REBOOTSTATEPATTERN, input),
                getValueFromXml(TOTALREBOOTSPATTERN, input));
    }

    private static int getValueFromXml(Pattern p, String input) {
        Matcher m = p.matcher(input);
        Preconditions.checkArgument(m.find());
        return Integer.parseInt(m.group(1));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(inputstate)
                .append(rebootstate)
                .append(relaystate)
                .append(totalreboots)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        EqualsBuilder e = new EqualsBuilder();
        WebRelayState other = (WebRelayState)obj;
        e.append(relaystate, other.relaystate);
        e.append(inputstate, other.inputstate);
        e.append(rebootstate, other.rebootstate);
        e.append(totalreboots, other.totalreboots);
        return e.isEquals();
    }

}
