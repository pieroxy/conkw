import m from 'mithril';

import { Children } from "mithril";
import { TextFilter } from '../../utils/TextFilter';
import { GenericInput } from './GenericInput';
import { HtmlInputEvent } from './HtmlInputEvent';
import { TextInputAttrs } from './TextFieldInput';

export class TextFieldInputWithSearch extends GenericInput<string, TextInputWithSearchAttrs> {
  private showList = false;
  private listHighlighted = "";
  private listDisplayed:string[] = [];
  private listHighlightedIndex = 0;

  render({attrs}: m.Vnode<TextInputWithSearchAttrs>): void | Children {
    let params:any = {
      type:"text",
      oninput: (e:HtmlInputEvent) => {
        this.setValue(e.target.value);
        this.computeErrorState();
        if (attrs.onchange) attrs.onchange();
      },
      value: this.getValueAsString(),
      autocapitalize:"none",
      onkeyup: (e: KeyboardEvent) => {
        if (e.code) {
          switch (e.code) {
            case 'Enter':
            case 'NumpadEnter':
              if (this.listHighlightedIndex) {
                this.setValue(this.listDisplayed[this.listHighlightedIndex-1]);
                this.listHighlightedIndex = 0;
                this.listHighlighted = "";
              } else {
                this.onenter(attrs);
              }
              break;
            case 'Escape':
              if (attrs.onescape) attrs.onescape();
              break;
            case 'Escape':
              if (attrs.onescape) attrs.onescape();
              break;
            case 'ArrowDown':
              this.listHighlightedIndex = Math.min(this.listDisplayed.length, this.listHighlightedIndex+1);
              this.listHighlighted = this.listDisplayed[this.listHighlightedIndex-1];
              break;
            case 'ArrowUp':
              this.listHighlightedIndex = Math.max(0, this.listHighlightedIndex-1);
              if (this.listHighlightedIndex == 0)
                this.listHighlighted = "";
              else
                this.listHighlighted = this.listDisplayed[this.listHighlightedIndex-1];
              break;
          }
        } else {
          if (e.keyCode == 13) {
            this.onenter(attrs);
          }
          if (e.keyCode == 27) {
            if (attrs.onescape) attrs.onescape();
          }
        }
        return true;
      },
      onfocus: () => {this.showList=true;},
      onblur: () => {setTimeout(() => {
        this.showList=false
        m.redraw();
      }, 200)}
    }
    if (attrs.placeholder) params.placeholder = attrs.placeholder;
    if (attrs.id) params.id = attrs.id;
    let tf = new TextFilter(this.getValueAsString());
    let idx=0;
    this.listDisplayed = [];

    return m(".textInputWithList", [
      this.showList?m(".textInputList", attrs.values
        .filter(v => tf.matchOne([v]))
        .map(v => {
          idx++;
          let h = idx == this.listHighlightedIndex;
          if (h) this.listHighlighted = v;
          this.listDisplayed.push(v);
          return m(TextFieldInputWithSearchListItem, {
            highlighted:h,
            onclick:() => {
              this.setValue(v);
            },
            value:v
          })
        })
      ):null,
      m("input" + this.getErrorClass() + (attrs.search ? ".searchbg":"") + (attrs.className ? attrs.className:""), params)
    ]);
  }
  onenter(attrs: TextInputWithSearchAttrs) {
    this.setValue(this.listHighlighted)
    attrs.onenter();
  }

  isEmpty(): boolean {
    return !this.getValueAsString()
  }

  oncreate(vnode:m.VnodeDOM<TextInputWithSearchAttrs>) {
    if (vnode.attrs.focus)
      (<any>vnode.dom).focus();
  }
}

export interface TextInputWithSearchAttrs extends TextInputAttrs {
  values:string[];
}

class TextFieldInputWithSearchListItem implements m.ClassComponent<TextFieldInputWithSearchListItemAttrs> {
  view(vnode: m.Vnode<TextFieldInputWithSearchListItemAttrs>): m.Children {
    if (vnode.attrs.highlighted) {
      let e = document.getElementById("list_item_"+vnode.attrs.value);
      if (e) {
        console.log("siv2 " + vnode.attrs.value);
        (<any>e).scrollIntoViewIfNeeded();
        }
    }
    return m(vnode.attrs.highlighted ? ".highlighted":"", {
      onclick:() => {
        vnode.attrs.onclick();
      },
      id:"list_item_"+vnode.attrs.value
    }, vnode.attrs.value);
  }
}

interface TextFieldInputWithSearchListItemAttrs {
  value:string;
  onclick:()=>void;
  highlighted:boolean;
}