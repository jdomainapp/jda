import { Component, Input, OnInit, Type} from '@angular/core';
import { BaseFormComponent } from './base-form/base-form.component';

@Component({
  selector: 'app-base',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.css']
})
export class BaseComponent implements OnInit {
  form_type: Type<any> = BaseFormComponent;
  constructor() { }
  title: string = 'Base';
  show_alert: boolean = false;
  alert_status: boolean = false;

  list_config: any = {
    apiName: '',
    columns:[]
  };

  search_id: any = '';
  child_view: string = 'form';
  deleteFlag: boolean = false;
  isOK: boolean = false;
  
  ngOnInit(): void {
    this.child_view= 'form';
  }

  mainClick(id: any = '') {
    this.child_view = 'form';
    this.search_id = id;
    // console.log(this.search_id);
  }

  browseClick(id: any = '') {
    this.search_id = id;
    this.child_view = 'list';
  }

  deleteClick() {
    this.deleteFlag = true;
    console.log(this.deleteFlag);
    // this.deleteFlag = false;
  }

  showAlert(status: any) {
    console.log('status', status);
    this.show_alert = false;
    this.show_alert = true;
    // this.FadeOutMsg();
    console.log('show alert:', this.show_alert);
    this.alert_status = status;
  }

}
