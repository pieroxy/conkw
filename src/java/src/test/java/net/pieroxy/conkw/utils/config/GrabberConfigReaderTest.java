package net.pieroxy.conkw.utils.config;

import junit.framework.TestCase;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GrabberConfigReaderTest extends TestCase {
    private final String TEST_SIMPLE = "{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":123,\"stringValue\":\"tsv\"}}";

    public void testNumber() {
        try {
            TestModel data = JsonHelper.getJson().deserialize(TestModel.class, new ByteArrayInputStream(TEST_SIMPLE.getBytes(StandardCharsets.UTF_8)));
            assertEquals(1, data.version);

            SimpleObjectWithFields o = new SimpleObjectWithFields();
            GrabberConfigReader.fillObject(o, data.object);
            assertEquals("tsv", o.getStringValue());
            assertEquals(123., o.getDoubleValue());
            assertTrue(o.getBoolValue());
        } catch (IOException e) {
            fail("Threw an exception : " + e.getMessage());
        }
    }
}
