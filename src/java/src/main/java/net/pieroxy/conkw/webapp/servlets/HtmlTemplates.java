package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.grabbers.Grabber;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

public class HtmlTemplates extends HttpServlet {
  private static List<Grabber> grabbers;

  public static void setGrabbers(List<Grabber> grabbers) {
    HtmlTemplates.grabbers = grabbers;
  }


  public static void close() {
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = req.getParameter("name");
    resp.setContentType("text/html;charset=utf-8");
    OutputStream out = resp.getOutputStream();
    Writer writer = new OutputStreamWriter(out, Charset.forName("UTF8"));
    if (name == null || name.trim().isEmpty() || name.equals("*")) {
      for (Grabber g : grabbers) {
        writer.write("<div class=\"section\" grabberName=\"");
        writer.write(g.getName());
        writer.write("\">");
        g.writeHtmlTemplate(writer);
        writer.write("</div>");
      }
    } else {
      for (Grabber g : grabbers) {
        if (g.getName().equals(name)) g.writeHtmlTemplate(writer);
      }
    }
    writer.close();
    out.close();
  }
}
