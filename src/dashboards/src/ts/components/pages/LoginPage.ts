import m from 'mithril';
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
  render():void | m.Children {
    return m(".loginpage", [
      m(".loginarea1", [
        m("img", {
          src:"/images/logo-white-400.png",
          style: {
            opacity:0.1,
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
            action:()=>{},
            form:this.form
          }, "Login")
        ]),
      ])
    ]);
  }
}
