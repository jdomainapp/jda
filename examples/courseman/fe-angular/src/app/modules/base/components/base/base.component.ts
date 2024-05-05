import { Component, EventEmitter, Injector, Input, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseService } from '../../services/base.service';
import { NotificationService } from '../../services/notification.service';
import { PatternService } from '../../pattern/pattern.service';

@Component({
  selector: 'app-base',
  template: '',
})
export class BaseComponent {
  @Input() apiName!: string;
  
  constructor(
    protected service: BaseService,
    protected route: ActivatedRoute,
    protected router: Router,
    protected notificationService: NotificationService,
    protected formBuilder: FormBuilder,
    protected patternService: PatternService,
  ) { }

  @Output('onHide') hideEvent = new EventEmitter();

  onHide(): void {
    this.hideEvent.emit();
  }

  renderItem(obj: any) {
    if (obj === null || obj === undefined) {
      return '';
    }

    if (obj instanceof Array) {
      return `(count: ${obj.length})`;
    }

    if (typeof obj === 'object') {
      // return JSON.stringify(obj);
      return Object.keys(obj)
        .map((key) => obj[key])
        .filter((value) => (typeof value !== 'object') && !(value instanceof Array))
        .reduce((k1, k2) => '' + k1 + ' | ' + k2, '');
    } else {
      return obj;
    }
  }

  deleteItem(id: string): void {
    this.service.delete(this.apiName, id).subscribe({
      next: (res) => {
        this.afterDelete(id);
        this.notificationService.showSuccess();
      },
      error: (e) => {
        this.notificationService.showError();
      }
    });
  }

  afterDelete(id: string): any {
    // to override
  }

  @Input() params: any = {};

  getParams(newParam: object): object {
    return { ...this.params, ...newParam }
  }
}
