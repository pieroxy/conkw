package net.pieroxy.conkw.utils.config;

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

    public void testNumbersTypes() {
        ObjectWithNumbers o = parseandFillInCustomProperty("{\"version\":2345, \"object\":{\"doubleObjectValue\":1.2345,\"intObjectValue\":12345,\"byteObjectValue\":12,\"shortObjectValue\":123,\"longObjectValue\":123456,\"floatObjectValue\":1.5432,\"doubleValue\":4.5678,\"intValue\":45678,\"byteValue\":45,\"shortValue\":4567,\"longValue\":456789,\"floatValue\":4.8765}}",
            2345, ObjectWithNumbers.class);

        assertEquals(1.2345, o.getDoubleObjectValue());
        assertEquals(Integer.valueOf(12345), o.getIntObjectValue());
        assertEquals(Byte.valueOf((byte)12), o.getByteObjectValue());
        assertEquals(Short.valueOf((short)123), o.getShortObjectValue());
        assertEquals(Long.valueOf(123456), o.getLongObjectValue());
        assertEquals(Float.valueOf(1.5432f), o.getFloatObjectValue());
        assertEquals(4.5678, o.getDoubleValue());
        assertEquals(45678, o.getIntValue());
        assertEquals(45, o.getByteValue());
        assertEquals(4567, o.getShortValue());
        assertEquals(456789, o.getLongValue());
        assertEquals(4.8765f, o.getFloatValue());


        /*





         */
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

    public void testSimpleListsErrors() {
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                "\"boolValues\":[true,false,\"false\",true,false,true]," +
                "\"doubleValues\":[123,2,6,4,2.5e12,-5]," +
                "\"stringValues\":[\"tsv\",\"tsv2\",\"\"]}}",
            4, ObjectWithSimpleList.class),
            RuntimeException.class,null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                    "\"boolValues\":[true,false,false,true,false,true]," +
                    "\"doubleValues\":[123,2,6,4,2.5e12,true]," +
                    "\"stringValues\":[\"tsv\",\"tsv2\",\"\"]}}",
                4, ObjectWithSimpleList.class),
            RuntimeException.class,null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                        "\"boolValues\":[true,false,false,true,false,true]," +
                        "\"doubleValues\":[123,2,6,4,2.5e12,-5]," +
                        "\"stringValues\":[\"tsv\",\"tsv2\",true]}}",
                4, ObjectWithSimpleList.class),
                RuntimeException.class,null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                        "\"boolValues\":true}}",
                4, ObjectWithSimpleList.class),
                RuntimeException.class,null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                        "\"stringValues\":\"true\"}}",
                4, ObjectWithSimpleList.class),
                RuntimeException.class,null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":4, \"object\":{" +
                        "\"doubleValues\":1.54}}",
                4, ObjectWithSimpleList.class),
                RuntimeException.class,null);
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

    public void testMaps() {
        ObjectWithMaps o = parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"ss\":{" +
                            "\"test\":\"123\"" +
                        "}, \"sb\":{" +
                            "\"testa\":true," +
                            "\"testb\":false" +
                        "}, \"sd\":{" +
                            "\"test1\":123," +
                            "\"test2\":1.001e100" +
                        "}, \"sc\":{" +
                            "\"testA\":{\"doubleValue\":2e200,\"doubleList\":[0.1,-1001]}," +
                            "\"testB\":null" +
                        "}}}",
                7, ObjectWithMaps.class);
        assertNotNull(o.getSs());
        assertEquals(1, o.getSs().size());
        assertEquals("123", o.getSs().get("test"));
        assertNotNull(o.getSd());
        assertEquals(2, o.getSd().size());
        assertEquals(123., o.getSd().get("test1"));
        assertEquals(1.001e100, o.getSd().get("test2"));
        assertNotNull(o.getSb());
        assertEquals(2, o.getSb().size());
        assertEquals(Boolean.TRUE, o.getSb().get("testa"));
        assertEquals(Boolean.FALSE, o.getSb().get("testb"));
        assertNotNull(o.getSc());
        assertEquals(2, o.getSc().size());
        assertNotNull(o.getSc().get("testA"));
        assertEquals(2e200, o.getSc().get("testA").getDoubleValue());
        assertEquals(2, o.getSc().get("testA").getDoubleList().size());
        assertEquals(.1, o.getSc().get("testA").getDoubleList().get(0));
        assertEquals(-1001d, o.getSc().get("testA").getDoubleList().get(1));
        assertNull(o.getSc().get("testB"));
    }

    public void testWronglyTypedMaps() {
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"ss\":{\"test\":123}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"ss\":{\"test\":true}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sb\":{\"test\":\"true\"}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sb\":{\"test\":123}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sd\":{\"test\":\"123\"}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sd\":{\"test\":true}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sd\":{\"test\":{}}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"sd\":{\"test\":[]}}}",
                7, ObjectWithMaps.class), RuntimeException.class, null);
    }

    public void testNullMaps() {
        ObjectWithMaps o = parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                        "\"ss\":null" +
                        ", \"sb\":null" +
                        ", \"sd\":null" +
                        ", \"sc\":null" +
                        "}}}",
                7, ObjectWithMaps.class);
        assertNull(o.getSs());
        assertNull(o.getSd());
        assertNull(o.getSb());
        assertNull(o.getSc());
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

    public void testCDuration() {
        ObjectWithCustomField o = parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                "\"object\":{" +
                "\"duration\":\"3d\"" +
                "}}}",
            7, ObjectWithCustomField.class);
        assertNotNull(o.getObject());
        assertEquals(CustomObject.class, o.getObject().getClass());
        assertNotNull(o.getObject().getDuration());
        assertEquals(3, o.getObject().getDuration().asDays());
    }

    public void testExistingValues() {
        TestModel data = parseJson("{\"version\":72, \"object\":{}}");
        assertEquals(72, data.version);
        assertNotNull(data.getObject());

        ObjectWithSimpleFields o = new ObjectWithSimpleFields();
        o.setDoubleValue(365.);
        o.setStringValue("abc123");
        GrabberConfigReader.fillObject(o, data.object);

        assertEquals(365., o.getDoubleValue());
        assertEquals("abc123", o.getStringValue());
    }

    public void testCustomFieldWithErrors() {
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                    "\"object\":{" +
                    "\"doubleValue\":123," +
                    "\"doubleList\":[]," +
                    "\"subObject\":{\"doublevalue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                    "\"customList\":[" +
                    "{\"doubleValue\":31,\"doubleList\":[1,null,-100]}," +
                    "{\"subObject\":{\"doubleValue\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                    "]}}}",
                7, ObjectWithCustomField.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                    "\"object\":{" +
                    "\"doubleValue\":123," +
                    "\"doubleList\":true," +
                    "\"subObject\":{\"doubleValue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                    "\"customList\":[" +
                    "{\"doubleValue\":31,\"doubleList\":[1,null,-100]}," +
                    "{\"subObject\":{\"doubleValue\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                    "]}}}",
                7, ObjectWithCustomField.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                    "\"object\":{" +
                    "\"doubleValue\":123," +
                    "\"doubleList\":[]," +
                    "\"subObject\":{\"doubleValue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                    "\"customList\":[" +
                    "{\"doubleValues\":31,\"doubleList\":[1,null,-100]}," +
                    "{\"subObject\":{\"doubleValue\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                    "]}}}",
                7, ObjectWithCustomField.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                    "\"object\":{" +
                    "\"doubleValue\":123," +
                    "\"doubleList\":[]," +
                    "\"subObject\":{\"doubleValue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                    "\"customList\":[" +
                    "{\"doubleValue\":31,\"doubleList\":[1,null,-100]}," +
                    "{\"subObject\":{\"doubleValues\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                    "]}}}",
                7, ObjectWithCustomField.class),
            RuntimeException.class, null);
        assertThrows(() -> parseandFillInCustomProperty("{\"version\":7, \"object\":{" +
                    "\"object\":{" +
                    "\"doubleValue\":123," +
                    "\"doubleList\":[]," +
                    "\"subObject\":{\"doubleValue\":2e200,\"doubleList\":[1,-100000000000000000000]}," +
                    "\"customList\":[" +
                    "\"Dude !\"," +
                    "{\"subObject\":{\"doubleValue\":2e210,\"doubleList\":[1,-1000000000000000000]}}" +
                    "]}}}",
                7, ObjectWithCustomField.class),
            RuntimeException.class, null);
    }

    public void testSuperClassFields() {
        ChildObject o = parseandFillInCustomProperty("{\"version\":17, \"object\":{\"childProperty\":\"CP\",\"motherProperty\":\"MP\"}}",
                17, ChildObject.class);
        assertNotNull(o.getMotherProperty());
        assertNotNull(o.getChildProperty());
        assertEquals("MP", o.getMotherProperty());
        assertEquals("CP", o.getChildProperty());
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
