import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-btn-delete',
  templateUrl: './btn-delete.component.html',
})
export class BtnDeleteComponent {
  @Output() confirmed = new EventEmitter();

  modalVisible: boolean = false;

  showModal(): void {
    this.modalVisible = true;
  }

  onCancel(): void {
    this.modalVisible = false;
  }

  onConfirm(): void {
    this.modalVisible = false;

    this.confirmed.emit();
  }

}
