package net.pieroxy.conkw.utils.config;

import junit.framework.TestCase;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GrabberConfigReaderTest extends TestCase {
    private final String TEST_SIMPLE = "{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":123,\"stringValue\":\"tsv\"}}";
    private final String TEST_SIMPLE_LISTS = "{\"version\":2, \"object\":{" +
            "\"boolValues\":[true,false,false,true,false,true]," +
            "\"doubleValues\":[123,2,6,4,2.5e12,-5]," +
            "\"stringValues\":[\"tsv\",\"tsv2\",\"\"]}}";

    public void testSimpleTypes() {
        try {
            TestModel data = JsonHelper.getJson().deserialize(TestModel.class, new ByteArrayInputStream(TEST_SIMPLE.getBytes(StandardCharsets.UTF_8)));
            assertEquals(1, data.version);

            SimpleObjectWithFields o = new SimpleObjectWithFields();
            GrabberConfigReader.fillObject(o, data.object);
            assertNotNull(o.getBoolValue());
            assertNotNull(o.getStringValue());
            assertNotNull(o.getDoubleValue());
            assertEquals("tsv", o.getStringValue());
            assertEquals(123., o.getDoubleValue());
            assertEquals(Boolean.TRUE, o.getBoolValue());
        } catch (IOException e) {
            fail("Threw an exception : " + e.getMessage());
        }
    }

    public void testSimpleLists() {
        try {
            TestModel data = JsonHelper.getJson().deserialize(TestModel.class, new ByteArrayInputStream(TEST_SIMPLE_LISTS.getBytes(StandardCharsets.UTF_8)));
            assertEquals(2, data.version);

            SimpleObjectWithSimpleList o = new SimpleObjectWithSimpleList();
            GrabberConfigReader.fillObject(o, data.object);
            assertNotNull(o.getBoolValues());
            assertNotNull(o.getStringValues());
            assertNotNull(o.getDoubleValues());
            assertEquals(3, o.getStringValues().size());
            assertEquals(6, o.getDoubleValues().size());
            assertEquals(6, o.getBoolValues().size());
            int index = 0;
            assertEquals("tsv", o.getStringValues().get(index++));
            assertEquals("tsv2", o.getStringValues().get(index++));
            assertEquals("", o.getStringValues().get(index++));
            index = 0;
            assertEquals(123., o.getDoubleValues().get(index++));
            assertEquals(2., o.getDoubleValues().get(index++));
            assertEquals(6., o.getDoubleValues().get(index++));
            assertEquals(4., o.getDoubleValues().get(index++));
            assertEquals(25e11, o.getDoubleValues().get(index++));
            assertEquals(-5., o.getDoubleValues().get(index++));
            index = 0;
            assertEquals(Boolean.TRUE, o.getBoolValues().get(index++));
            assertEquals(Boolean.FALSE, o.getBoolValues().get(index++));
            assertEquals(Boolean.FALSE, o.getBoolValues().get(index++));
            assertEquals(Boolean.TRUE, o.getBoolValues().get(index++));
            assertEquals(Boolean.FALSE, o.getBoolValues().get(index++));
            assertEquals(Boolean.TRUE, o.getBoolValues().get(index++));
        } catch (IOException e) {
            fail("Threw an exception : " + e.getMessage());
        }
    }
}
