import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: '@slot{{selector}}',
  templateUrl: '@slot{{html-path}}', //./address-form.component.html
})
export class @slot{{componentName}} extends BaseFormComponent implements FormComponent{
    // item: any = {};  
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

	override apiName = '@slot{{api}}';
	
	@loop{subview}[[
    @Input() @slot{{field}}Id: string = '';    
    ngAfterViewInit() {
      if (this.@slot{{field}}Id) {
        this.@slot{{getSubFunction}}(this.@slot{{field}}Id);
        console.log('Student: ', this.item.@slot{{field}});
      }
    }
             
    @slot{{getSubFunction}}(event: any) {
      console.log(event);
      this.service.init('@slot{{subAPI}}');
      this.getItem(event, '@slot{{field}}');
      this.service.init(this.apiName);
    }  
    ]]loop{subview}@
      
  }
