import { Component, OnInit } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-address',
  templateUrl: './address.component.html',
})
export class AddressComponent extends BaseManagerComponent {
  override title = 'Address';
  
}