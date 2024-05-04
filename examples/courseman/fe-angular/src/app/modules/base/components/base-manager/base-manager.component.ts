import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-base-manager',
  template: '',
})
export class BaseManagerComponent {
  title = 'Title';

  view = 'form';
  changeView(view: string): void {
    this.view = view;
  }

  searchId: string = '';
  edit(id: any): void {
    this.searchId = id;

    this.view = 'form';
  }

  @Input() params: any = {};

  getParams(newParam: object): object {
    return { ...this.params, ...newParam }
  }

  @Input() belongsTo: any = '';

  @Input() viewMode: string = 'default';

  @Output('onEmbedded') embeddedEvent = new EventEmitter();

  @Input() menuPrefix: string = '';

}
