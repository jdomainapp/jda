import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BaseCommonComponent } from '../base-common/base-common.component';

@Component({
  selector: 'app-base-list',
  templateUrl: './base-list.component.html',
  styleUrls: ['./base-list.component.css'],
})
export class BaseListComponent extends BaseCommonComponent implements OnInit {
  @Input() config = {
    apiName: '',
    columns: [],
  };

  @Input() override list: any;

  @Output() goDetail: EventEmitter<string> = new EventEmitter<string>();

  public columns: any = [];

  override ngOnInit(): void {
    this.columns = this.config.columns;
  }

  ngAfterViewInit(): void {
    console.log('Base lits config', this.config);
    this.apiName = this.config.apiName;
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
