import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NotificationService } from 'src/app/common/notification.service';
import { BaseListComponent } from '../base-list/base-list.component';
import { BaseService } from '../base.service';

@Component({
  selector: 'app-base-common',
  template: '',
})
export class BaseCommonComponent implements OnInit  {
  constructor(
    protected service: BaseService,
    private toastr: NotificationService
  ) {
  }

  list: any = '';
  item: any = {}; //dữ liệu để bind với các control
  search_id: any = '';
  apiName: any = '';
  // @Output() success: EventEmitter<boolean> = new EventEmitter<boolean>();
  status: any = false;
  // show_alert: boolean = false;

  ngOnInit(): void {
  }

  returnStatus(status: boolean) {
    this.status = status;
    if (status) {
      this.toastr.showSuccess();
    } else {
      this.toastr.showError();
    }
    console.log(status);
  }

  renderItem(obj: any) {
    if (obj === null || obj === undefined) {
      return '';
    }
    if (typeof obj === 'object') {
      // return JSON.stringify(obj);
      return Object.keys(obj)
        .map((key) => obj[key])
        .reduce((k1, k2) => '' + k1 + ' | ' + k2);
    } else {
      return obj;
    }
  }

  updateItem() {
    console.log('BaseComponent :', this.apiName);
    this.service.update(this.apiName, this.item).subscribe((response) => {
      if (typeof response.id === 'undefined') {
        this.returnStatus(false); //Không lưu được
      } else {
        this.returnStatus(true); //Lưu được
      }
      console.log('RES: ', response);
    });
  }

  createItem() {
    //console.log(this.item);
    this.service.create(this.apiName, this.item).subscribe((response) => {
      if (typeof response.id === 'undefined') {
        this.returnStatus(false); //Không lưu được
      } else {
        this.returnStatus(true); //Lưu được
      }
      console.log('RES: ', response);
    });
  }

  getList() {
    const list: BaseListComponent = this as unknown as BaseListComponent;
    this.search_id = '';
    this.service.get(this.apiName).subscribe((response) => {
      console.log('RES: ', response.content);
      this.list = response.content;
    });
  }

  getAllChilds(parentId: any, childrenApi: string) {
    this.service
      .getAllChilds(this.apiName, parentId, childrenApi)
      .subscribe((response) => {
        console.log('RES: ', response.content);
        this.list = response.content;
      });
  }

  delete(id: number) {
    this.service.delete(this.apiName, id).subscribe((response) => {
      console.log('RES: ', response);
      this.getList();
    });
  }

  getItem(search_id: any, key: string = '', apiName?: string) {
    this.service
      .search(apiName ?? this.apiName, search_id)
      .subscribe((response) => {
        console.log('RES: ', response);
        if (key) {
          this.item[key] = response;
        } else {
          this.item = response;
        }
      });
  }
}
