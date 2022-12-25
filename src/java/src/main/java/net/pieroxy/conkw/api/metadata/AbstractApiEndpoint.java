package net.pieroxy.conkw.api.metadata;

import net.pieroxy.conkw.api.model.ApiResponse;
import net.pieroxy.conkw.api.model.ApiResultCodes;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.exceptions.ApiError;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.exceptions.PermissionDeniedException;
import net.pieroxy.conkw.utils.exceptions.SwallowIOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractApiEndpoint<I,O> implements ApiEndpoint {
  private Logger LOGGER = Logger.getLogger(this.getClass().getName());

  private final Class<I> inputType;
  private UserRole roleAllowed;
  private String userAgent;
  private String ipAdress;

  public abstract O process(I input, User user) throws Exception;

  public final Class<I> buildType() {
    Class<?> subClass = getClass();
    while (subClass.getSuperclass() != AbstractApiEndpoint.class) {
      // instance.getClass() is no subclass of classOfInterest or instance is a direct instance of classOfInterest
      subClass = subClass.getSuperclass();
      if (subClass == null) throw new IllegalArgumentException();
    }
    ParameterizedType parameterizedType = (ParameterizedType) subClass.getGenericSuperclass();
    return (Class<I>) parameterizedType.getActualTypeArguments()[0];
  }

  public AbstractApiEndpoint() {
    roleAllowed = this.getClass().getAnnotation(Endpoint.class).role();
    inputType = buildType();
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  public void process(HttpServletRequest req, HttpServletResponse res, User user) {
    try {
      userAgent = req.getHeader("User-Agent");
      ipAdress = req.getRemoteAddr();
      UserRole ur = UserRole.ANONYMOUS;
      if (user!=null && user.getRole()!=null) ur = user.getRole();
      if (!ur.doesRoleContains(roleAllowed)) {
        throw new PermissionDeniedException(this, user, roleAllowed);
      }
      I input;
      if (req.getMethod().equals("GET")) {
        input = JsonHelper.readFromString(inputType, req.getParameter("input"));
      } else if (req.getMethod().equals("POST")) {
        input = JsonHelper.readFromInputStream(inputType, req.getInputStream());
      } else {
        throw new ApiError("Method " + req.getMethod() + " is not accepted");
      }
      res.setContentType(getContentType());
      res.setStatus(HttpServletResponse.SC_OK);
      O resultObject = process(input, user);
      JsonHelper.writeToOutputStream(ApiResponse.buildOkResult(resultObject), res.getOutputStream());
      if (resultObject instanceof Closeable) {
        try {
          ((Closeable) resultObject).close();
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "", e);
        }
      }
    } catch (DisplayMessageException e) {
      res.setContentType(getJsonContentType());
      getLogger().log(Level.FINE, "", e);
      SwallowIOException.run(() -> JsonHelper.writeToOutputStream(ApiResponse.buildErrResult(ApiResultCodes.DISPLAY_ERROR, e.getMessage()), res.getOutputStream()));
    } catch (PermissionDeniedException e) {
      res.setContentType(getJsonContentType());
      getLogger().log(Level.INFO, "", e);
      SwallowIOException.run(() -> JsonHelper.writeToOutputStream(ApiResponse.buildErrResult(ApiResultCodes.DISPLAY_ERROR, "A resource access was denied."), res.getOutputStream()));
    } catch (Exception e) {
      res.setContentType(getJsonContentType());
      getLogger().log(Level.SEVERE, "", e);
      SwallowIOException.run(() -> JsonHelper.writeToOutputStream(ApiResponse.buildErrResult(ApiResultCodes.TECH_ERROR, e.getMessage()), res.getOutputStream()));
    }
  }

  private String getContentType() {
    return getJsonContentType();
  }
  private String getJsonContentType() {
    return "application/json";
  }

  public String getUserAgent() {
    return userAgent;
  }

  public String getIpAdress() {
    return ipAdress;
  }
}
