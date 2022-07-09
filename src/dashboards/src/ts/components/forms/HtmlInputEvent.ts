export interface HtmlInputEvent extends Event {
  target:InputEventTarget;
}

export interface InputEventTarget extends EventTarget {
  value:string;
}