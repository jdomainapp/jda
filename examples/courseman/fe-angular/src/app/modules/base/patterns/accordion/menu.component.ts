import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls:['./menu.styles.css'],
})
export class MenuComponent {
  @Input() items: any[] = [];
  @Input() prefix: string = '';
}