package net.pieroxy.conkw.api.metadata;

import net.pieroxy.conkw.api.model.ApiResultCodes;
import net.pieroxy.conkw.api.model.ApiResponse;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.exceptions.ApiError;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractApiEndpoint<I,O> {
  private Logger LOGGER = Logger.getLogger(this.getClass().getName());
  public abstract O process(I input) throws Exception;
  public abstract Class<I> getType();

  Logger getLogger() {
    return LOGGER;
  }

  protected void process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    try {
      I input;
      if (req.getMethod().equals("GET")) {
        input = JsonHelper.readFromString(getType(), req.getParameter("input"));
      } else if (req.getMethod().equals("POST")) {
        input = JsonHelper.readFromInputStream(getType(), req.getInputStream());
      } else {
        throw new ApiError("Method " + req.getMethod() + " is not accepted");
      }
      O output = process(input);
      res.setContentType(getContentType());
      res.setStatus(HttpServletResponse.SC_OK);
      JsonHelper.writeToOutputStream(ApiResponse.buildOkResult(output), res.getOutputStream());
    } catch (DisplayMessageException e) {
      res.setContentType(getJsonContentType());
      getLogger().log(Level.WARNING, "", e);
      JsonHelper.writeToOutputStream(ApiResponse.buildErrResult(ApiResultCodes.DISPLAY_ERROR, e.getMessage()), res.getOutputStream());
    } catch (Exception e) {
      res.setContentType(getJsonContentType());
      getLogger().log(Level.SEVERE, "", e);
      JsonHelper.writeToOutputStream(ApiResponse.buildErrResult(ApiResultCodes.TECH_ERROR, e.getMessage()), res.getOutputStream());
    }
  }

  private String getContentType() {
    return getJsonContentType();
  }
  private String getJsonContentType() {
    return "application/json";
  }
}
