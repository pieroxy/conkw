package net.pieroxy.conkw.utils;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class RemoveJsonCommentsInputStreamTest extends TestCase {

    public void testSimple() throws Exception {
        assertEquals("toto", filter("toto"));
        assertEquals("toto", filter("toto// blu"));
        assertEquals("toto", filter("to/*bibuba*/to// blu"));
    }

    public void testOnelineComment() throws Exception {
        assertEquals("toto\nblah\n", filter("toto\n//This is a comment\nblah\n"));
        assertEquals("toto\nblah\n", filter("//another comment\ntoto\n//This is a comment\nblah\n"));
    }

    public void testMultilineComment() throws Exception {
        assertEquals("toto\n\nblah\n", filter("toto\n/*This is a comment*/\nblah\n"));
        assertEquals("toto\nblah\n", filter("//another comment\ntoto\n//This is a comment\nblah\n"));
    }

    public void testMLCommentInString() throws Exception {
        assertEquals("\"tot/*oblah\"", filter("\"tot/*oblah\""));
    }

    public void testOLCommentInString() throws Exception {
        assertEquals("\"tot//oblah\"", filter("\"tot//oblah\""));
    }

    private String filter(String input) throws IOException {
        InputStream is = new ByteArrayInputStream(input.getBytes(Charset.forName("UTF8")));
        InputStream is2 = new RemoveJsonCommentsInputStream(is, "test");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (true) {
            int i = is2.read();
            if (i==-1) break;
            os.write(i);
        }
        return new String(os.toByteArray(), Charset.forName("UTF8"));
    }
}
