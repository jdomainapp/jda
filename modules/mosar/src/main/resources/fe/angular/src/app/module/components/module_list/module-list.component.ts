import { Component } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-@slot{{moduleJname}}-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class @slot{{ModuleName}}ListComponent extends BaseListComponent {
  override apiName = "@slot{{moduleJnames}}";
  
  override columns = [@loop{columnConfigs}[[
    { field: '@slot{{fieldName}}', title: '@slot{{fieldLabel}}' },
  ]]loop{columnConfigs}@
  ];
}
