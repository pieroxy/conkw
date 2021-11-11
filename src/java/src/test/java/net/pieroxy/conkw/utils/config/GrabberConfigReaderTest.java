package net.pieroxy.conkw.utils.config;

import junit.framework.TestCase;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GrabberConfigReaderTest extends TestCase {
    private final String TEST_SIMPLE = "{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":123,\"stringValue\":\"tsv\"}}";
    private final String TEST_NULLS = "{\"version\":2, \"object\":{\"boolValue\":null,\"doubleValue\":null,\"stringValue\":null}}";
    private final String TEST_MISSING = "{\"version\":3, \"object\":{}}";
    private final String TEST_SIMPLE_LISTS = "{\"version\":4, \"object\":{" +
        "\"boolValues\":[true,false,false,true,false,true]," +
        "\"doubleValues\":[123,2,6,4,2.5e12,-5]," +
        "\"stringValues\":[\"tsv\",\"tsv2\",\"\"]}}";
    private final String TEST_SIMPLE_LISTS_WITH_NULLS = "{\"version\":5, \"object\":{" +
        "\"boolValues\":[true,null]," +
        "\"doubleValues\":[123,null]," +
        "\"stringValues\":[\"tsv\",null]}}";

    public void testSimpleTypes() {
        TestModel data = parseJson(TEST_SIMPLE);
        assertEquals(1, data.version);

        SimpleObjectWithFields o = new SimpleObjectWithFields();
        GrabberConfigReader.fillObject(o, data.object);
        assertNotNull(o.getBoolValue());
        assertNotNull(o.getStringValue());
        assertNotNull(o.getDoubleValue());
        assertEquals("tsv", o.getStringValue());
        assertEquals(123., o.getDoubleValue());
        assertEquals(Boolean.TRUE, o.getBoolValue());
    }

    public void testSimpleNulls() {
        TestModel data = parseJson(TEST_NULLS);
        assertEquals(2, data.version);

        SimpleObjectWithFields o = new SimpleObjectWithFields();
        GrabberConfigReader.fillObject(o, data.object);
        assertNull(o.getBoolValue());
        assertNull(o.getStringValue());
        assertNull(o.getDoubleValue());
    }

    public void testMissing() {
        TestModel data = parseJson(TEST_MISSING);
        assertEquals(3, data.version);

        SimpleObjectWithFields o = new SimpleObjectWithFields();
        GrabberConfigReader.fillObject(o, data.object);
        assertNull(o.getBoolValue());
        assertNull(o.getStringValue());
        assertNull(o.getDoubleValue());
    }

    public void testSimpleLists() {
        TestModel data = parseJson(TEST_SIMPLE_LISTS);
        assertEquals(4, data.version);

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
    }

    public void testSimpleListsWithNulls() {
        TestModel data = parseJson(TEST_SIMPLE_LISTS_WITH_NULLS);
        assertEquals(5, data.version);

        SimpleObjectWithSimpleList o = new SimpleObjectWithSimpleList();
        GrabberConfigReader.fillObject(o, data.object);
        assertNotNull(o.getBoolValues());
        assertNotNull(o.getStringValues());
        assertNotNull(o.getDoubleValues());
        assertEquals(2, o.getStringValues().size());
        assertEquals(2, o.getDoubleValues().size());
        assertEquals(2, o.getBoolValues().size());
        int index = 0;
        assertEquals("tsv", o.getStringValues().get(index++));
        assertEquals(null, o.getStringValues().get(index++));
        index = 0;
        assertEquals(123., o.getDoubleValues().get(index++));
        assertEquals(null, o.getDoubleValues().get(index++));
        index = 0;
        assertEquals(Boolean.TRUE, o.getBoolValues().get(index++));
        assertEquals(null, o.getBoolValues().get(index++));
    }

    TestModel parseJson(String json) {
        try {
            return JsonHelper.getJson().deserialize(TestModel.class, new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
