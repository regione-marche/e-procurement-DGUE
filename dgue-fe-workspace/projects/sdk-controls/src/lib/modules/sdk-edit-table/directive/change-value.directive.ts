import { Directive, ElementRef, EventEmitter, Input, Output } from "@angular/core";

@Directive({
    selector: '[changeD]',
  })
  export class ChangeDirective {
    private _value: string;
  
    @Output('input') public input$: EventEmitter<string> = new EventEmitter();
  
    constructor(private el: ElementRef) {}
  
    private get element(): HTMLInputElement {
      return this.el != null ? this.el.nativeElement : null;
    }
  
    @Input('data') public set value(value: string) {
      this._value = value;      
      this.element.value = parseFloat(this.value).toFixed(2);;
      this.input$.emit(this.value);
    }
  
    public get value() {
      return this._value;
    }
  }
  