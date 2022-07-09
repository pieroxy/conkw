import m from 'mithril';
import { Form } from '../forms/Form';
import { TextFieldInput } from '../forms/TextFieldInput';
import { AbstractPage } from './AbstractPage';

export class LoginPage extends AbstractPage<any> {
  username:string="";
  password:string="";


  getPageTitle(): string {
    return "ConkW - Login";
  }
  render():void | m.Children {
    let form = new Form();
    return m(".loginpage", [
      m("img", {
        src:"/images/logo-white-400.png",
        style: {
          opacity:0.1,
          width:"300px"
        }
      }),
      m(".title", "Please log in"),
      m("", [
        m(".inputlabel", "Username"),
        m(TextFieldInput, {
          refHolder: this,
          refProperty: "username",
          form:form,
          requiredMessage:"Please enter your login"
        }, ""),
      ]),
    ]);
  }
}
