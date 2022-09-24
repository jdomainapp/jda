import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[compHost]',
})
export class CompDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}

