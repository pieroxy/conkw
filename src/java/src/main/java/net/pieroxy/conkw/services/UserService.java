package net.pieroxy.conkw.services;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.UserLogin;
import net.pieroxy.conkw.config.HashedSecret;
import net.pieroxy.conkw.config.LocalStorageManager;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.hashing.HashTools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UserService {
  private final static Logger LOGGER = Logger.getLogger(UserService.class.getName());

  public static final int ITERATIONS = 32;
  public static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA512";
  public static final int HASH_KEYSIZE = 512;
  public static final String ADMIN_ID="admin";
  public static final String ADMIN_DEFAULT_PASSWORD="password";
  private final File userStorage;
  private final File credsStorage;

  public UserService(LocalStorageManager localStorageManager) {
    userStorage = new File(localStorageManager.getConfDir(), "users.json");
    credsStorage = new File(localStorageManager.getConfDir(), "usersCredentials.json");
    initialize();
  }

  private void initialize() {
    if (userStorage.exists() &&
        userStorage.length()>3 &&
        credsStorage.exists() &&
        credsStorage.length()>3) return;

    synchronized (this.userStorage) {
      Users users = new Users();
      User admin = new User();
      admin.setId(ADMIN_ID);
      users.getDataByLogin().put(ADMIN_ID, admin);
      save(users);
      LOGGER.info("Initialized default admin user");
    }

    synchronized (this.credsStorage) {
      UserCredentials creds = new UserCredentials();
      UserCredential cred = new UserCredential();
      cred.setUserLogin(ADMIN_ID);
      HashedSecret hs = new HashedSecret();
      hs.setTempPassword(ADMIN_DEFAULT_PASSWORD);
      cred.setHashedSecret(hs);
      creds.getDataByLogin().put(ADMIN_ID, cred);
      save(creds);
      LOGGER.info("Initialized default admin credentials");
    }
  }

  private void save(UserCredentials creds) {
    try {
      JsonHelper.writeToFile(creds, credsStorage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void save(Users users) {
    try {
      JsonHelper.writeToFile(users, userStorage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  UserCredentials readCredentials()  {
    try {
      return JsonHelper.readFromFile(UserCredentials.class, this.credsStorage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  Users readUsers()  {
    try {
      return JsonHelper.readFromFile(Users.class, this.userStorage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public UserLogin performAuthentication(String login, String password) {
    UserCredential uc = readCredentials().getDataByLogin().get(login);
    if (uc==null) return new UserLogin(null, true);
    HashedSecret hs = uc.getHashedSecret();
    if (!StringUtil.isNullOrEmptyTrimmed(hs.getTempPassword())) {
      // Simple password check
      if (password.equals(hs.getTempPassword()) &&
          (hs.getTempPasswordExpiration()==null ||
              hs.getTempPasswordExpiration().getTime() > System.currentTimeMillis())) {
        return new UserLogin(readUsers().getDataByLogin().get(login), true);
      }
    } else {
      String hashed = HashTools.hashPassword(password, hs);
      if (hashed.equals(hs.getHashedSecret())) {
        return new UserLogin(readUsers().getDataByLogin().get(login), false);
      }
    }
    return new UserLogin(null, true);
  }

  public void changePassword(User user, String password) {
    UserCredential uc = new UserCredential();
    HashedSecret hs = new HashedSecret();
    hs.setSalt(HashTools.getRandomSequence(8));
    hs.setIterations(ITERATIONS);
    hs.setMethod(HASH_ALGORITHM);
    hs.setKeySize(HASH_KEYSIZE);
    hs.setHashedSecret(HashTools.hashPassword(password, hs));

    uc.setUserLogin(user.getId());
    uc.setHashedSecret(hs);

    synchronized (this.credsStorage) {
      UserCredentials creds = new UserCredentials();
      creds.getDataByLogin().put(user.getId(), uc);
      save(creds);
    }
  }
}

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class Users {
  Map<String,User> dataByLogin = new HashMap<>();

  public Map<String, User> getDataByLogin() {
    return dataByLogin;
  }

  public void setDataByLogin(Map<String, User> dataByLogin) {
    this.dataByLogin = dataByLogin;
  }
}

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class UserCredentials {
  Map<String,UserCredential> dataByLogin = new HashMap<>();

  public Map<String, UserCredential> getDataByLogin() {
    return dataByLogin;
  }

  public void setDataByLogin(Map<String, UserCredential> dataByLogin) {
    this.dataByLogin = dataByLogin;
  }
}

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class UserCredential {
  String userLogin;
  private HashedSecret hashedSecret;

  public String getUserLogin() {
    return userLogin;
  }

  public void setUserLogin(String userLogin) {
    this.userLogin = userLogin;
  }

  public HashedSecret getHashedSecret() {
    return hashedSecret;
  }

  public void setHashedSecret(HashedSecret hashedSecret) {
    this.hashedSecret = hashedSecret;
  }
}