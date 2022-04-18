package net.pieroxy.conkw.config;

public interface CredentialsProvider {
  Credentials getCredentials();
  String getCredentialsRef();
}
