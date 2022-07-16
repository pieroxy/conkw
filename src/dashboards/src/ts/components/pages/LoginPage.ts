import m from 'mithril';
import { DoLoginEndpointInput, DoLoginEndpointOutput } from '../../auto/pieroxy-conkw';
import { AppVersion } from '../../auto/version';
import { Api } from '../../utils/api/Api';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../utils/Notifications';
import { Button } from '../forms/Button';
import { Form } from '../forms/Form';
import { PasswordFieldInput } from '../forms/PasswordFieldInput';
import { TextFieldInput } from '../forms/TextFieldInput';
import { AbstractPage } from './AbstractPage';

export class LoginPage extends AbstractPage<any> {
  username:string="";
  password:string="";
  private form = new Form();


  getPageTitle(): string {
    return "ConkW - Login";
  }
  render():m.Children {
    return m(".loginpage", [
      m(".loginarea1", [
        m("img", {
          src:"/images/logo-white-400.png",
          style: {
            opacity:0.4,
            width:"300px"
          }
        }),
      ]),
      m(".loginarea2", [
        m(".title", "Please log in"),
        m("", [
          m(".inputlabel", "Username"),
          m(TextFieldInput, {
            refHolder: this,
            refProperty: "username",
            form:this.form,
            requiredMessage:"Please enter your login"
          }),
        ]),
        m("", [
          m(".inputlabel", "Password"),
          m(PasswordFieldInput, {
            refHolder: this,
            refProperty: "password",
            form:this.form,
            requiredMessage:"Please enter your password"
          }),
        ]),
        m("", [
          m(Button, {
            action:()=>{
              Api.call<DoLoginEndpointInput, DoLoginEndpointOutput>({
                method:"POST",
                endpoint:"DoLogin",
                body:{
                  login:this.username,
                  password:this.password
                }
              }).then((_data:DoLoginEndpointOutput) => {
                Notifications.addNotification(new Notification(NotificationsClass.LOGIN, NotificationsType.INFO, "Welcome", 5))
              })
            },
            form:this.form
          }, "Login")
        ]),
      ]),
      m(".footer", [
        m("span", m("a", {href:"/doc/README.html"}, "Documentation")),
        m("span", m("a", {href:"https://github.com/pieroxy/conkw"}, "Github")),
        m("span", "Ver: ", AppVersion.MVN_VER, " (", AppVersion.GIT_REV, ", " + AppVersion.GIT_DEPTH + ")")
      ])
    ]);
  }
}
