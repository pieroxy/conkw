package net.pieroxy.conkw.utils.config;

import junit.framework.TestCase;
import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.utils.JsonHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GrabberConfigReaderTest extends ConkwTestCase {

    public void testSimpleTypes() {
        ObjectWithSimpleFields o = parseandFillInCustomProperty("{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":123,\"stringValue\":\"tsv\"}}",
            1, ObjectWithSimpleFields.class);
        assertNotNull(o.getBoolValue());
        assertNotNull(o.getStringValue());
        assertNotNull(o.getDoubleValue());
        assertEquals("tsv", o.getStringValue());
        assertEquals(123., o.getDoubleValue());
        assertEquals(Boolean.TRUE, o.getBoolValue());
    }

    public void testSimpleTypesErrors() {
        assertThrows(() -> parseandFillInCustomProperty(
                "{\"version\":1, \"object\":{\"boolValue\":2,\"doubleValue\":123,\"stringValue\":\"tsv\"}}", 1, ObjectWithSimpleFields.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty(
                "{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":\"123\",\"stringValue\":\"tsv\"}}", 1, ObjectWithSimpleFields.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty(
                "{\"version\":1, \"object\":{\"boolValue\":true,\"doubleValue\":123,\"stringValue\":false}}", 1, ObjectWithSimpleFields.class),
            RuntimeException.class, null);
    }

    public void testSimpleNulls() {
        ObjectWithSimpleFields o = parseandFillInCustomProperty("{\"version\":2, \"object\":{\"boolValue\":null,\"doubleValue\":null,\"stringValue\":null}}",
            2, ObjectWithSimpleFields.class);
        assertNull(o.getBoolValue());
        assertNull(o.getStringValue());
        assertNull(o.getDoubleValue());
    }

    public void testMissing() {
        ObjectWithSimpleFields o = parseandFillInCustomProperty("{\"version\":3, \"object\":{}}", 3, ObjectWithSimpleFields.class);
        assertNull(o.getBoolValue());
        assertNull(o.getStringValue());
        assertNull(o.getDoubleValue());
    }

    public void testSimpleLists() {
        ObjectWithSimpleList o = parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
            "\"boolValues\":[true,false,false,true,false,true]," +
            "\"doubleValues\":[123,2,6,4,2.5e12,-5]," +
            "\"stringValues\":[\"tsv\",\"tsv2\",\"\"]}}",
            4, ObjectWithSimpleList.class);
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
        ObjectWithSimpleList o = parseandFillInCustomProperty("{\"version\":5, \"object\":{" +
                "\"boolValues\":[true,null]," +
                "\"doubleValues\":[123,null]," +
                "\"stringValues\":[\"tsv\",null]}}",
            5, ObjectWithSimpleList.class);
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

    public void testSimpleNullLists() {
        ObjectWithSimpleList o = parseandFillInCustomProperty("{\"version\":6, \"object\":{" +
                "\"boolValues\":null," +
                "\"doubleValues\":null," +
                "\"stringValues\":null}}",
            6, ObjectWithSimpleList.class);
        assertNull(o.getBoolValues());
        assertNull(o.getStringValues());
        assertNull(o.getDoubleValues());
    }

    public void testCustomField() {
        ObjectWithCustomField o = parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                "\"object\":{" +
                "\"doubleValue\":123," +
                "\"doubleList\":[]," +
                "\"subObject\":{\"doubleValue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                "\"customList\":[" +
                "{\"doubleValue\":31,\"doubleList\":[1,null,-100]}," +
                "{\"subObject\":{\"doubleValue\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                "]}}}",
            7, ObjectWithCustomField.class);
        assertNotNull(o.getObject());
        assertEquals(CustomObject.class, o.getObject().getClass());
        assertEquals(123., o.getObject().getDoubleValue());
        assertNotNull(o.getObject().getDoubleList());
        assertEquals(0, o.getObject().getDoubleList().size());
        assertNotNull(o.getObject().getSubObject());
        assertEquals(2e200, o.getObject().getSubObject().getDoubleValue());
        assertNotNull(o.getObject().getSubObject().getDoubleList());
        assertEquals(2, o.getObject().getSubObject().getDoubleList().size());
        int index = 0;
        assertEquals(1.0, o.getObject().getSubObject().getDoubleList().get(index++));
        assertEquals(-1e20, o.getObject().getSubObject().getDoubleList().get(index++));

        assertNotNull(o.getObject().getCustomList());
        assertEquals(2, o.getObject().getCustomList().size());

        assertNotNull(o.getObject().getCustomList().get(0));
        assertEquals(31., o.getObject().getCustomList().get(0).getDoubleValue());
        assertNotNull(o.getObject().getCustomList().get(0).getDoubleList());
        assertEquals(3, o.getObject().getCustomList().get(0).getDoubleList().size());
        index = 0;
        assertEquals(1., o.getObject().getCustomList().get(0).getDoubleList().get(index++));
        assertEquals(null, o.getObject().getCustomList().get(0).getDoubleList().get(index++));
        assertEquals(-100., o.getObject().getCustomList().get(0).getDoubleList().get(index++));

        assertNotNull(o.getObject().getCustomList().get(1));
        assertNotNull(o.getObject().getCustomList().get(1).getSubObject());
        assertEquals(2e210, o.getObject().getCustomList().get(1).getSubObject().getDoubleValue());
        assertNotNull(o.getObject().getCustomList().get(1).getSubObject().getDoubleList());
        index = 0;
        assertEquals(2, o.getObject().getCustomList().get(1).getSubObject().getDoubleList().size());
        assertEquals(1., o.getObject().getCustomList().get(1).getSubObject().getDoubleList().get(index++));
        assertEquals(-1e18, o.getObject().getCustomList().get(1).getSubObject().getDoubleList().get(index++));
    }

    TestModel parseJson(String json) {
        try {
            return JsonHelper.getJson().deserialize(TestModel.class, new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parseandFillInCustomProperty(String s, int i, Class<T> type) {
        TestModel data = parseJson(s);
        assertEquals(i, data.version);

        T o = null;
        try {
            o = type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GrabberConfigReader.fillObject(o, data.object);
        return o;
    }

}
