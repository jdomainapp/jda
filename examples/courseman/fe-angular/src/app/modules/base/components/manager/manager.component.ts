import { Component, ContentChild, Input, TemplateRef } from '@angular/core';

@Component({
  selector: 'app-manager',
  templateUrl: './manager.component.html',
})
export class ManagerComponent {
  @Input() title = '';

  @ContentChild('form') formTemplate!: TemplateRef<any>;
  @ContentChild('list') listTemplate!: TemplateRef<any>;

  view = 'form';
  setView(view: string): void {
    this.view = view;
  }

  item: any = '';
  selectItem = (value: any) => {
    this.item = value;
    // show form
    this.view = 'form';
  }
}
