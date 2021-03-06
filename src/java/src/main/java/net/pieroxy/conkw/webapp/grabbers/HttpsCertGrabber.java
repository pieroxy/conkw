package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// Bluntly copy pasted from https://gist.github.com/sureshpai/8c762603969e78dc2c68
public class HttpsCertGrabber extends TimeThrottledGrabber {
  private CDuration CACHE_TTL = CDurationParser.parse("3h");

  private String names;

  // usage : SSLCertExpiry host <cn>
  // does a http://host request.
  // takes an optional cn variable (which defaults to host)

  public static void main(String[] args) throws Exception {
    if (args.length <1){
      System.out.println("Usage: SSLCertExpiry <host> [<CN>]");
      return;
    }
    String cn = args[0];
    if (args.length > 1){
      cn = args[2];
    }
    Date expiresOn = null;
    expiresOn = getExpirationDate(cn);

    if (expiresOn!=null) {
      Date now = new Date();
      System.out.println(args[0] + " certificate expires on :" + expiresOn + ".. only " +
          (expiresOn.getTime() - now.getTime()) / (1000 * 60 * 60 * 24) + " days to go");
    } else {
      System.out.println("Could not find " + cn + " in cert.");
    }
  }

  private static Date getExpirationDate(String cn) throws IOException, CertificateParsingException {
    // without a trust manager, i was having problems of
    // sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
    // the code below was taken from
    // http://stackoverflow.com/questions/7443235/getting-java-to-accept-all-certs-over-https
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {

          public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
          }

          public void checkServerTrusted(X509Certificate[] arg0, String arg1)
              throws CertificateException {
            // TODO Auto-generated method stub

          }

          public void checkClientTrusted(X509Certificate[] arg0, String arg1)
              throws CertificateException {
            // TODO Auto-generated method stub

          }
        }
    };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      System.out.println("could not install trust manager.. continuing here; it may not be necessary");
    }


    URL url=new URL("https://"+ cn);
    HttpsURLConnection conn= (HttpsURLConnection)url.openConnection();
    conn.connect();
    Certificate[] certs = conn.getServerCertificates();
    for(Certificate c:certs){
      //System.out.println(c.getType());
      //System.out.println(c.toString());
      X509Certificate xc = (X509Certificate)c; // we should really check the type beore doing this typecast..
      Set<String> dn = new HashSet<>();
      dn.add(xc.getSubjectDN().getName());
      if (xc.getSubjectAlternativeNames()!=null)
        xc.getSubjectAlternativeNames().forEach(l -> {
          if (l == null)
            System.out.println("nullList");
          else
            l.stream().forEach(e -> {
              if (e instanceof String) dn.add((String)e);
              //System.out.println("Class " + e == null ? null : e.getClass().getName());
              //System.out.println("Value " + e);
            });
        });

      if (dn.contains(cn)){
        return xc.getNotAfter();
      }
    }
    return null;
  }

  @Override
  public String getDefaultName() {
    return "httpscert";
  }

  @Override
  protected void setConfig(Map<String, String> config) {
    names = config.get("names");
  }

  @Override
  protected CDuration getTtl() {
    return CACHE_TTL;
  }

  @Override
  protected void load(ResponseData res) {
    long now = System.currentTimeMillis();
    Arrays.stream(names.split(",")).forEach(s -> {
      try {
        Date exp = getExpirationDate(s);
        if (exp!=null) {
          res.addMetric("date_" + s, exp.getTime());
          res.addMetric("days_" + s, (exp.getTime() - now) / (1000 * 60 * 60 * 24));
          res.addMetric("ts_" + s, exp.getTime() - now);
        }
      } catch (Exception e) {
        res.addError(e.getMessage());
      }
    });
    res.addMetric("alldomains", names);
  }

  @Override
  protected String getCacheKey() {
    return names;
  }
}
