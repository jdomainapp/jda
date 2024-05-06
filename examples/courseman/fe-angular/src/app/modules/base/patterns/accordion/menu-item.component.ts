import { AccordionService } from './accordion.service';
import { Component, Input, SimpleChanges } from '@angular/core';
import { NavigationEnd, Router, Scroll } from '@angular/router';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.styles.css'],
})
export class MenuItemComponent {
  @Input() item: any = {};
  @Input() prefix: string = '';

  @Input() searching: boolean = false;
  isCollapsed = true;
  isActive = false;

  constructor(private router: Router, private accordionService: AccordionService) {
    
  }

  ngOnInit() {
    if (this.searching) {
      this.isCollapsed = false;
    }

    this.router.events.subscribe((event) => {
      if (event instanceof Scroll) {
        if (event.anchor?.startsWith(this.item.endpoint)) {
          // if (event.anchor?.startsWith(this.prefix+"-"+this.item.endpoint)) {
          this.isCollapsed = false;
          this.isActive = true;
        } else {
          this.isActive = false;
        }
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.searching) {
      this.isCollapsed = false;
    }
  }
}