export class TextFilter {
  private words:string[]=[];
  constructor(text:string) {
    this.update(text);
  }

  private filterInput(s:string):string {
    return s
        .toLowerCase()
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  }

  public update(text:string):void {
    if (text) this.words = text.split(" ").map(this.filterInput);
    else this.words = [];
  }

  public matchOne(input:string[]):boolean {
    input = input.map(this.filterInput);

    for (let idx in this.words) {
      let found = false;
      for (let idxInput in input) {
        let text = input[idxInput];
        if (text.indexOf(this.words[idx])>-1) {
          found = true;
          break;
        }
      }
      if (!found) return false;
    }
    return true;
  }
}