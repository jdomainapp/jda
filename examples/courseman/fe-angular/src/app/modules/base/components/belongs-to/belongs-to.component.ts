import { Component, Input } from '@angular/core';
import { BaseComponent } from '../base/base.component';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-belongs-to',
  templateUrl: './belongs-to.component.html',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    multi: true,
    useExisting: BelongsToComponent
  }]
})
export class BelongsToComponent extends BaseComponent implements ControlValueAccessor {
  @Input() label: string = '';

  private _item: any = '';

  set item(value: any) {
    this._item = value;
    this.searchId = this._item?.id;
  }
  get item() {
    return this._item;
  }

  searchId: string = '';

  findById() {
    this.service.getById(this.apiName, this.searchId).subscribe(data => {
      if (!data['content']) { // since api return all if no id
        this.item = data;

        this.onChange(this.item);
      };
    });
  }


  /* ControlValueAccessor */
  onChange = (item: any) => { };

  onTouched = () => { };

  touched = false;

  disabled = false;

  writeValue(obj: any): void {
    this.item = obj;
  }
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  // TODO: implements ontouched, disabled, validators https://blog.angular-university.io/angular-custom-form-controls/
  /* END ControlValueAccessor */
}
