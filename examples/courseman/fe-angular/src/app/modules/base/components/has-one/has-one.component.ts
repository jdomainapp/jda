import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { ControlValueAccessor, FormGroup, NG_VALUE_ACCESSOR } from '@angular/forms';
import { BaseComponent } from '../base/base.component';

@Component({
  selector: 'app-has-one',
  templateUrl: './has-one.component.html',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    multi: true,
    useExisting: HasOneComponent
  }]
})
export class HasOneComponent extends BaseComponent implements ControlValueAccessor {
  @Input() label: string = '';
  @ContentChild('subform') subformTemplate!: TemplateRef<any>;

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

  formVisible: boolean = false;

  toggleForm(): void {
    this.formVisible = !this.formVisible;
  }

  unlink() {
    this.item = {};
    this.onChange({});
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