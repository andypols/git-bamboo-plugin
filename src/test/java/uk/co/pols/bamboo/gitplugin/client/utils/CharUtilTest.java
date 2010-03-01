package uk.co.pols.bamboo.gitplugin.client.utils;

import junit.framework.TestCase;
import uk.co.pols.bamboo.gitplugin.client.utils.CharUtil;

public class CharUtilTest extends TestCase {
    private static final String TEST_STRING = "Test ...";

    public void testConvertsAByteArrayIntoACharacterArray() {
        assertEqualArrays(
                new char[] { 'T', 'e', 's', 't', ' ', '.', '.', '.'},
                CharUtil.toCharArray(TEST_STRING.getBytes())
        );
    }

    private void assertEqualArrays(char[] lhs, char[] rhs) {
        assertEquals(lhs.length, rhs.length);
        for(int index = 0; index < lhs.length; index++) {
            assertEquals(lhs[index], rhs[index]);
        }
    }
}
