import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { Auth } from '../../../utils/Auth';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../../utils/Notifications';
import { Button } from '../../atoms/forms/Button';
import { Form } from '../../atoms/forms/Form';
import { PasswordFieldInput } from '../../atoms/forms/PasswordFieldInput';
import { TextFieldInput } from '../../atoms/forms/TextFieldInput';
import { LogoFullIcon } from '../../atoms/icons/LogoFull';
import { AbstractPage } from '../AbstractPage';
import { LoginFooter } from './LoginFooter';

export class LoginPage extends AbstractPage<any> {
  username:string="";
  password:string="";
  private form = new Form();


  getPageTitle(): string {
    return "ConkW - Login";
  }
  render():m.Children {
    return m(".loginpage", [
      m(".loginarea1", m(LogoFullIcon)),
      m(".loginarea2", [
        m(".title", "Please log in"),
        m("", [
          m(".inputlabel", "Username"),
          m(TextFieldInput, {
            refHolder: this,
            refProperty: "username",
            form:this.form,
            requiredMessage:"Please enter your login",
            onenter:()=>{document.getElementById("pass")?.focus();},
            spellcheck:false
          }),
        ]),
        m("", [
          m(".inputlabel", "Password"),
          m(PasswordFieldInput, {
            refHolder: this,
            id:"pass",
            refProperty: "password",
            form:this.form,
            requiredMessage:"Please enter your password",
            onenter:()=>{
              this.doLogin()
            }
          }),
        ]),
        m("", [
          m(Button, {
            action:()=>{
              this.doLogin()
            },
            form:this.form
          }, "Login")
        ]),
      ]),
      m(LoginFooter)
    ]);
  }

  private doLogin() {
    ApiEndpoints.DoLogin.call({
        login:this.username,
        password:this.password
      }
    ).then((data) => {
      if (data.passwordMustChangeNow) {
        Notifications.addNotification(new Notification(NotificationsClass.LOGIN, NotificationsType.SUCCESS, "Welcome to conkw.", 5))
        Routing.goToScreen(Endpoints.PASSWORD_CHANGE, {id:this.username});
      } else {
        Notifications.addNotification(new Notification(NotificationsClass.LOGIN, NotificationsType.SUCCESS, "Welcome back!", 5))
        Auth.setAuthToken(data.token, data.user);
        Routing.goToScreen(Endpoints.HOME);
      }
    })
  }
}
