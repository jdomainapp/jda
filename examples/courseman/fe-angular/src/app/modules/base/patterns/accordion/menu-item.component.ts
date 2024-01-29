import { Component, Input } from '@angular/core';
import { NavigationEnd, Router, Scroll } from '@angular/router';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.styles.css'],
})
export class MenuItemComponent {
  @Input() item: any = {};
  @Input() prefix: string = '';

  isCollapsed = true;
  isActive = false;

  constructor(private router: Router) {
  
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof Scroll) {
        if (event.anchor == this.prefix+"-"+this.item.endpoint) {
          this.isCollapsed = true;
          this.isActive = true;
        } else {
          this.isActive = false;
        }
      }
    });
  }
}