import { GenericInput } from "./GenericInput";

export class Form {
  private elements:GenericInput<any,any>[]=[];
  public add(i:GenericInput<any,any>):GenericInput<any,any> {
    for (let c=0 ; c<this.elements.length ; c++) {
      if (this.elements[c] === i) return i;
    }
    this.elements.push(i);
    return i;
  }

  public validate():boolean {
    let res = true;
    for (let e of this.elements) {
      if (e.isRequired() && e.isEmpty()) {
        e.setHighlight(true);
        res = false;
      } else {
        e.setHighlight(false);
      }
    }
    return res;
  }
}