package uk.co.pols.bamboo.gitplugin.client.commands;

public class CharUtil {

    /**
     * Converts byte array to char array.
     */
    public static char[] toCharArray(byte[] barr) {
        if (barr == null) {
            return null;
        }
        char[] carr = new char[barr.length];
        for (int i = 0; i < barr.length; i++) {
            carr[i] = (char) barr[i];
        }
        return carr;
    }
}
