import m from 'mithril';
import { TextFieldInput } from '../forms/TextFieldInput';
import { AbstractPage } from './AbstractPage';

export class LoginPage extends AbstractPage<any> {
  username:string="";
  password:string="";


  getPageTitle(): string {
    return "ConkW - Login";
  }
  render():m.Children {
    return m(".loginpage", [
      m(".title", "Please log in"),
      m("", [
        m(".inputlabel", "Username"),
        m(<any>TextFieldInput, {
          refHolder:this,
          refProperty:"username"
        })
      ]),
    ]);
  }
}

