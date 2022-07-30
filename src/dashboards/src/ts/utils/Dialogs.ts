import m from "mithril"
import { Button } from "../components/forms/Button";
import { Form } from "../components/forms/Form";
import { TextFieldInput } from "../components/forms/TextFieldInput";

export class Dialogs {
  private static current:DialogRender[] = [];

  public static getCurrent():DialogRender|null {
    if (Dialogs.current.length) return Dialogs.current[0];
    return null;
  }
  public static getToBeDisplayed():DialogRender[] {
    return this.current;
  }

  public static dismiss():void {
    Dialogs.current.pop();
  }

  public static add(d:DialogRender) {
    if (Dialogs.current.length == 0 || Dialogs.current[0].key !== d.key)
      Dialogs.current.push(d);
  }

  public static prompt<T>(msg:string,value:string,parse:(value:string) => T|Error,onok:(value:T) => void):void {
    Dialogs.add(new PromptDialog(msg,value,parse,onok));
  }
  public static confirm(msg:string,yes:string,no:string,onok:() => any,onko?:() => any):void {
    Dialogs.add(new ConfirmDialog(msg,yes,no,onok, onko));
  }
}

export abstract class DialogRender {
  protected abstract view():m.Vnode<any,any>;
  abstract key:string;
  abstract title?:m.Children;

  public render():m.Vnode<any,any> {
    return m(".dialogout", m(".dialogin", this.view()));
  }
}

class ConfirmDialog extends DialogRender {
  key:string;
  yes:string;
  no:string;
  title:undefined;
  onok:() => any;
  onko?:() => any;
  
  constructor(msg:string,yes:string,no:string,onok:() => any,onko?:() => any) {
    super();
    this.key = msg;
    this.no = no;
    this.yes = yes;
    this.onok = onok;
    this.onko = onko;
  }
  public view():m.Vnode<any,any> {
    let onok = this.onok;
    let onko = this.onko;
    return m("", [m("", this.key), m(".buttonpanel", [
      m(Button, {
        action:()=>{onok();Dialogs.dismiss();}
      }, this.yes),
      m(Button, {
        action:function(){if (onko) onko();Dialogs.dismiss();}
      }, this.no)
    ])]);
  }
}

class PromptDialog<T> extends DialogRender {
  key:string;
  msg:string;
  value:string;
  message:string="";
  parse:(value:string) => T|Error;
  done:(value:T) => void;
  title:undefined;
  
  constructor(msg:string,value:string,parse:(value:string) => T|Error,onok:(value:T) => void) {
    super();
    this.key = "Prompt:"+msg;
    this.msg = msg;
    this.parse = parse;
    this.done = onok;
    this.value = value;
  }
  public view():m.Vnode<any,any> {

    let ok = () => {
      let res = this.parse(this.value);
      if (res instanceof Error) {
        this.message = "⛔  " + res.message;
      } else {
        this.done(res);
        Dialogs.dismiss();
      }
    }

    return m("", [
      m("", this.msg), 
      m(TextFieldInput, {
        form:new Form(),
        refHolder:this,
        refProperty:"value",
        onenter:() => {ok()},
        onescape:() => Dialogs.dismiss(),
        focus:true,
        spellcheck:false
      }),
      m(".buttonpanel", [
        m(Button, {
          action:ok
        }, "OK"),
        m(Button, {
          secondary:true,
          action:function(){Dialogs.dismiss();}
        }, "Cancel")
    ])]);
  }
}
