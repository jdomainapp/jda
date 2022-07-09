import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-@slot{{moduleJname}}-form',
  templateUrl: './@slot{{moduleJname}}-form.component.html',
})
export class @slot{{ModuleName}}FormComponent extends BaseFormComponent implements FormComponent{
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }
@loop{declareLinkedDomainId}[[
    @Input() @slot{{linkedDomain}}Id: string = '';]]loop{declareLinkedDomainId}@

    override apiName = '@slot{{moduleJnames}}';
@loop{linkedDomainShowFlags}[[
    @slot{{linked_domain}}_flag: boolean = false;]]loop{linkedDomainShowFlags}@

    ngAfterViewInit() {@loop{getLinkedDomainData}[[
      if (this.@slot{{linkedDomain}}Id) {
        this.get@slot{{LinkedDomain}}(this.@slot{{linkedDomain}}Id);
      }]]loop{getLinkedDomainData}@
    }
@loop{getLinkedDomainMethods}[[
    get@slot{{LinkedDomain}}(event: any) {
      this.getItem(event, '@slot{{linkedDomain}}', '@slot{{linkedJdomains}}');
    }]]loop{getLinkedDomainMethods}@
@loop{changeShowLinkedDomainFlagMethods}[[
    change@slot{{LinkedDomain}}Flag() {
      this.@slot{{linked_domain}}_flag = !this.@slot{{linked_domain}}_flag;
    }]]loop{changeShowLinkedDomainFlagMethods}@
  }
