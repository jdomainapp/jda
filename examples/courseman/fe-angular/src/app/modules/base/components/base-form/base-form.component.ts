import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { BaseComponent } from '../base/base.component';
import { FormGroup } from '@angular/forms';
import { AccordionFactory } from '../../patterns/accordion/accordion.factory';

@Component({
  selector: 'app-base-form',
  template: '',
})
export abstract class BaseFormComponent extends BaseComponent {
  @Input() searchId: string = '';
  
  protected form!: FormGroup;

  _item: any = {};
  @Input()
  set item(value: any) {
    this._item = value;

    this.createForm();
  }
  get item(): any {
    return this._item;
  }

  // @Output() itemChange = new EventEmitter<any>();

  abstract createForm(): void;

  submitted: boolean = false;

  onSubmit(): void {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }

    this.save();
  }

  onReset(): void {
    this.submitted = false;

    this.newItem();
    // TODO: to original data if editing
    
  }

  ngOnInit(): void {

    this.createForm();

    if (this.searchId) {
      this.getById();
    }

    // bind passed data
    if (this.params) {
      this.form.patchValue(this.params);
    }

    this.patternService.addConsumer(AccordionFactory.createProviderConsumer(
      { host: this }
    ));
  }

  getById(): void {
    this.service.getById(this.apiName, this.searchId).subscribe({
      next: (res) => {
        this.item = res;
      }
    });
  }
  
  newItem(): void {
    this.searchId = '';
    this.item = {};

    // bind passed data
    if (this.params) {
      this.form.patchValue(this.params);
    }
  }

  save(): void {
    // remove empty objects
    let data = this.form.value;
    data = Object.keys(data)
      .filter((k) => !(data[k] && typeof data[k] === 'object' && Object.keys(data[k]).length == 0))
      .reduce((a, k) => ({ ...a, [k]: data[k] }), {});
  
    if (!this.item?.id) {
      this.createItem(data);
    } else {
      this.updateItem(data);
    }
  }

  createItem(data: any): void {
    this.service.create(this.apiName, data).subscribe({
      next: (res) => {
        this.item = res;

        this.notificationService.showSuccess();
      },
      error: () => this.notificationService.showError()
    })
  }

  updateItem(data: any): void{
    this.service.update(this.apiName, this.item.id, data).subscribe({
      next: (res) => {
        // this.item = res; // since api not return updated item
        
        this.notificationService.showSuccess();
      },
      error: () => this.notificationService.showError()
    });
  }

  override afterDelete(id: any): void {
    this.searchId = '';
    this.item = {};
  }

  /** view mode */
  @Input() viewMode: string = 'default';

  /** accordion menu */
  menus: any[] = [];
  @Input() prefix: string = '';
}
