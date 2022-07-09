import { GenericInput } from "./GenericInput";

export class Form {
  public elements:GenericInput<any,any>[]=[];
  public add(i:GenericInput<any,any>):GenericInput<any,any> {
    for (let c=0 ; c<this.elements.length ; c++) {
      if (this.elements[c] === i) return i;
    }
    this.elements.push(i);
    return i;
  }

  public validate():boolean {
    let res = true;
    let messages = "";
    for (let e of this.elements) {
      if (e.isInErrorState()) {
        messages += e.getRequiredMessage() + "\n";
        e.setHighlight(true);
        res = false;
      } else {
        e.setHighlight(false);
      }
    }
    if (!res && messages) {
      alert(messages);
    }
    return res;
  }
}