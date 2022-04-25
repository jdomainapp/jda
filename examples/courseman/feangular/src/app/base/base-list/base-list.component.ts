import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { BaseService} from 'src/app/base/base.service';
import { NotificationService } from 'src/app/common/notification.service';
import { BaseCommonComponent } from '../base-common/base-common.component';

@Component({
  selector: 'app-base-list',
  templateUrl: './base-list.component.html',
  styleUrls: ['./base-list.component.css'],
})
export class BaseListComponent extends BaseCommonComponent implements OnInit {
  @Input() config = {
    apiName: '',
    columns: []
  };

  @Output() goDetail: EventEmitter<string> = new EventEmitter<string>();
  
  public columns: any = [];
  // constructor(protected serv: BaseService, tst: NotificationService) {
  //   super(serv, tst);
  // }

  override ngOnInit(): void {
    this.service.init(this.config.apiName);
    this.columns = this.config.columns;
    this.getList();
  }

  show_modal: boolean = false;
  delete_id: any = '';

  showModal(id: any) {
    this.show_modal = true;
    this.delete_id = id;
  }
 
  handleOK() {
    this.show_modal = false;
    this.delete(this.delete_id);
  }

  handleCancel() {
    this.show_modal = false;
  }
  changeToDetails(id: any) {
    // console.log(id);
    this.goDetail.emit(id);
  }
}

