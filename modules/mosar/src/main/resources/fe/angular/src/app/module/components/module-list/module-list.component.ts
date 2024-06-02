import { Component } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-address-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class ModuleListComponent extends BaseListComponent {
  override apiName = "addresses";
  
  override columns = [
    { field: 'id', title: 'ID' },
    { field: 'name', title: 'City name' },
    { field: 'student', title: 'Student' },
  ];
}
