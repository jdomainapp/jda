import { Component, Input, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { BaseService } from 'src/app/base/base.service';
import { NotificationService } from 'src/app/common/notification.service';
import { BaseCommonComponent } from '../base-common/base-common.component';

@Component({
  selector: 'app-base-form',
  template: '',
  styleUrls: ['./base-form.component.css']
})
export class BaseFormComponent extends BaseCommonComponent implements OnInit {
  show_child: boolean = false;
  @Input() show_component: boolean = true;

  // constructor(protected serv: BaseService, tst: NotificationService) {
  //   super(serv, tst);
  // }

  override ngOnInit(): void {
    this.item ={};
    // this.loadComponent();
  }

  reset() {
    this.item = {};
  }

  save() {
      //dữ liệu từ child gửi vào
      if (typeof this.item.id != 'undefined' && this.item.id) {
        console.log(`Update item on ${this.apiName}  :${this.item}`);
        this.updateItem();
      } else {
        console.log('create:', this.item);
        this.createItem();
      }
    }

  search(search_id: any) {   
      this.getItem(search_id);
  }
  
  changeShowChild() {
    this.show_child = !this.show_child;
  }
}
