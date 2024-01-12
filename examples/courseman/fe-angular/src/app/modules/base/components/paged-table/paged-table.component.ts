import { Component, ContentChild, Input, OnInit, SimpleChanges, TemplateRef } from '@angular/core';

@Component({
  selector: 'app-paged-table',
  templateUrl: './paged-table.component.html',
})
export class PagedTableComponent {
  private _items: any[] = [];
  @Input()
  set items(value: any[]) {
    this._items = value;

    this.paginate();
  }
  get items(): any[] {
    return this._items;
  } 

  @ContentChild('row') rowTemplate!: TemplateRef<any>;

  pagedItems: any[] = [];
  page = 1;
  pageSizes = [5, 10, 20];
  pageSize = this.pageSizes[0];
  count = 0;

  paginate(): void {
    if (!this.items) return;
    
    this.count = this.items.length;

    if (this.page) {
      const skip = (this.page - 1) * this.pageSize;
      this.pagedItems = this.items.slice(skip, Math.min(skip + this.pageSize, this.count));
    }
  }

  onPageChange(event: number): void {
    this.page = event;

    this.paginate();
  }

  onPageSizeChange(event: any): void {
    this.pageSize = event.target.value;
    this.page = 1;

    this.paginate();
  }

}
