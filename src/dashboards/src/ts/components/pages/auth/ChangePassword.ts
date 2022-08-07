import m from 'mithril';
import { ApiEndpoints } from '../../../auto/ApiEndpoints';
import { IdAttrs } from '../../../utils/attrs/IdAttrs';
import { Auth, AuthenticationStatus } from '../../../utils/Auth';
import { Endpoints } from '../../../utils/navigation/Endpoints';
import { Routing } from '../../../utils/navigation/Routing';
import { Button } from '../../atoms/forms/Button';
import { Form } from '../../atoms/forms/Form';
import { PasswordFieldInput } from '../../atoms/forms/PasswordFieldInput';
import { LogoFullIcon } from '../../atoms/icons/LogoFull';
import { AbstractPage } from '../AbstractPage';
import { LoginFooter } from './LoginFooter';

export class ChangePassword extends AbstractPage<IdAttrs> {
  getPageTitle(): string {
    return "ConkW - Change your password";
  }
  render({attrs}:m.Vnode<IdAttrs>):m.Children {
    return m(".loginpage", [
      Auth.getAuthenticationStatus() === AuthenticationStatus.LOGGED_IN ?
      m(".loginarea1", m(ChangePasswordForm, {id:attrs.id})) :
      [
        m(".loginarea1", m(LogoFullIcon)),
        m(".loginarea2", [
          m(ChangePasswordForm, {id:attrs.id})
        ]),
        m(LoginFooter)
      ]
    ]);
  }

}

class ChangePasswordForm implements m.ClassComponent<IdAttrs> {
  p1:string="";
  p2:string="";
  p3:string="";
  private form = new Form();

  view({attrs}:m.Vnode<IdAttrs>) {
    return [
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
          requiredMessage:"Please enter your new password",
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
        }, "Do it !")
      ]),
    ]
  }

  private doChangePassword(login:string) {
    ApiEndpoints.DoChangePassword.call({
        login:login,
        actual:this.p1,
        a:this.p2,
        b:this.p3
      }
    ).then(() => {
      Routing.goToScreen(Endpoints.LOGIN);
    })
  }
}