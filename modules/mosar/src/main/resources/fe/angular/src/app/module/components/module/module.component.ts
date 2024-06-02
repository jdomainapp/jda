import { Component } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-address',
  templateUrl: './module.component.html',
})
export class ModuleComponent extends BaseManagerComponent {
  override title = 'Address';

}