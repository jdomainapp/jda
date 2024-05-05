import { Component, EventEmitter, Input, Output, SimpleChanges, Type } from '@angular/core';
import { BaseComponent } from '../base/base.component';
import { AutoSearchFactory } from '../../patterns/autosearch/autosearch.factory';

@Component({
  selector: 'app-base-list',
  templateUrl: './base-list.component.html',
})
export class BaseListComponent extends BaseComponent {

  @Input('belongsTo') apiPrefix: string = ''; 

  columns: any[] = [];

  @Output('onItemSelect') itemSelectEvent = new EventEmitter();
  onItemSelect(id: string): void {
    this.itemSelectEvent.emit(id);
  }

  private _items: any[] = [];
  set items(data: any[]) {
    this._items = data;
    this.filter();

    this.patternService.onDataChange({items: this.items});
  }
  get items(): any[] {
    return this._items;
  }

  ngOnInit(): void {
    this.getList();

    this.patternService.addConsumer(AutoSearchFactory.createProviderConsumer({ host: this }));
  }

  getList(): void {
    this.service.getList(this.apiPrefix + this.apiName).subscribe(res => {
      const { content } = res;
      this.items = content;
    });
  }

  /* filter */
  filteredItems: any[] = [];
  searchId: string = '';
  searchKeyword: string = '';

  filter(): void {
    this.filteredItems = this.items;

    if (this.searchId) {
      this.filteredItems = this.filteredItems.filter(item => item.id == this.searchId);
    }

    if (this.searchKeyword) {
      this.filteredItems = this.filteredItems.filter(item => {
        for (const key in item) {
          if (item[key] && typeof item[key] !== 'object') {
            if ((this.renderItem(item[key]) + "").toLowerCase().includes(this.searchKeyword.toLowerCase())) {
              return true;
            }
          }
        }

        return false;
      });
    }
  }
  /* END filter */

  override afterDelete(id: any): void {
    this.items = this.items.filter(item => item.id !== id);
  }

}
