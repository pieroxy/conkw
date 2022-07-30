import m from 'mithril';
import { DoChangePasswordInput, DoChangePasswordOutput } from '../../../auto/pieroxy-conkw';
import { Api } from '../../../utils/api/Api';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { Button } from '../../forms/Button';
import { Form } from '../../forms/Form';
import { PasswordFieldInput } from '../../forms/PasswordFieldInput';
import { AbstractPage } from '../AbstractPage';
import { LoginFooter } from './LoginFooter';

export class ChangePassword extends AbstractPage<IdAttrs> {
  p1:string="";
  p2:string="";
  p3:string="";
  private form = new Form();

  getPageTitle(): string {
    return "ConkW - Change your password";
  }
  render({attrs}:m.Vnode<IdAttrs>):m.Children {
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
        m(".title", "Change your password"),
        m("", [
          m(".inputlabel", "Current password"),
          m(PasswordFieldInput, {
            refHolder: this,
            refProperty: "p1",
            form:this.form,
            requiredMessage:"Please enter your current password",
            onenter:()=>{document.getElementById("p2")?.focus();}
          }),
        ]),
        m("", [
          m(".inputlabel", "New password"),
          m(PasswordFieldInput, {
            refHolder: this,
            id:"p2",
            refProperty: "p2",
            form:this.form,
            requiredMessage:"Please enter your new password twice",
            onenter:()=>{document.getElementById("p3")?.focus();}
          }),
        ]),
        m("", [
          m(".inputlabel", "New password again"),
          m(PasswordFieldInput, {
            refHolder: this,
            id:"p3",
            refProperty: "p3",
            form:this.form,
            requiredMessage:"Please enter your new password twice",
            onenter:()=>{this.doChangePassword(attrs.id);}
          }),
        ]),
        m("", [
          m(Button, {
            action:()=>{
              this.doChangePassword(attrs.id);
            },
            form:this.form
          }, "Enregistrer")
        ]),
      ]),
      m(LoginFooter)
    ]);
  }

  private doChangePassword(login:string) {
    Api.call<DoChangePasswordInput, DoChangePasswordOutput>({
      method:"POST",
      endpoint:"DoChangePassword",
      body:{
        login:login,
        actual:this.p1,
        a:this.p2,
        b:this.p3
      }
    }).then(() => {
      Routing.goToScreen(Endpoints.LOGIN);
    })
  }
}
