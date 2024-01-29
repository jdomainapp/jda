import { AccordionComponent } from './accordion.component';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'FragmentComponent',
  template: '<ng-content></ng-content>',
})
export class FragmentComponent {

  constructor(private parent: AccordionComponent) {
    this.parent.items.push(this);
  }
}