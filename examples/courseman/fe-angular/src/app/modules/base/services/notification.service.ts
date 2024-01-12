import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private toastr: ToastrService) { }

  showSuccess() {
    this.toastr.success('<p>Successfully performed operation!</p>', 'Notification', {
      enableHtml :  true
    });
  }

  showError() {
    this.toastr.error('<p>Operation Failed.</p>', 'Notification', {
      enableHtml :  true
    });
  }  
}
