import { Component } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-@slot{{moduleJname}}',
  templateUrl: './@slot{{moduleJname}}.component.html',
})
export class @slot{{ModuleName}}Component extends BaseManagerComponent {
  override title = '@slot{{Module__Name}}';

}