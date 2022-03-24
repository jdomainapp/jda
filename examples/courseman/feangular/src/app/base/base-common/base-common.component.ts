import { Component, EventEmitter, OnInit} from '@angular/core';
import { BaseService } from '../base.service';
import { NotificationService } from 'src/app/common/notification.service';

@Component({
  selector: 'app-base-common',
  template: '',
})
export class BaseCommonComponent implements OnInit {

  constructor(protected service: BaseService, private toastr: NotificationService) {
  }

  list: any;
  item: any = {}; //dữ liệu để bind với các control
  search_id: any = '';
  apiName: any = '';
  // @Output() success: EventEmitter<boolean> = new EventEmitter<boolean>();
  status: any = false;
  // show_alert: boolean = false;

  ngOnInit(): void {
    // this.service_init();
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

  renderItem(obj:any) {
    if (obj === null || obj === undefined) {
      return "";
    }
    if (typeof (obj) === "object") {
      // return JSON.stringify(obj);
      return Object.keys(obj)
        .map(key => obj[key])
        .reduce((k1, k2) => "" + k1 + " | " + k2);
    } else {
      return obj;
    }
  }

  updateItem() {
    console.log('update:', this.item);
    this.service.update(this.item).subscribe(
      response => {
        if (typeof response.id === 'undefined') {
          this.returnStatus(false); //Không lưu được
        } else {
          this.returnStatus(true); //Lưu được
        }        
        console.log('RES: ', response);   
      }
    )    
  }

  createItem() {
    //console.log(this.item);
    this.service.create(this.item).subscribe(
      response => {
        if (typeof response.id === 'undefined') {
          this.returnStatus(false); //Không lưu được
        } else {
          this.returnStatus(true); //Lưu được
        }
        console.log('RES: ', response)
      }
    )
  }

  getList() {
    this.search_id = '';
    this.service.get().subscribe(
      response => {
        console.log('RES: ', response.content)
        this.list = response.content;
      }
      )
  } 

  delete(id: number) {
    this.service.delete(id).subscribe(
      response => {
        console.log('RES: ', response);
        this.getList();
      }
     )
 }

  getItem(search_id: any, key: string='') {
    this.service.search(search_id).subscribe(
      response => {
        console.log('RES: ', response)
        if (key) {
          this.item[key] =  response;
        } else {
          this.item = response;
        }        
      }
    )
  }
}
