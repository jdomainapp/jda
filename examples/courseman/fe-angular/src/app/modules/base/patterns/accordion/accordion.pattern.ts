import { ComponentRef } from "@angular/core";
import { Subject, takeUntil } from "rxjs";
import { ViewRegionComponent } from 'src/app/modules/base/pattern/view-region';
import { Pattern } from "../../pattern/pattern";
import { AccordionComponent } from "./accordion.component";
import { ModelRegionDirective } from "../../pattern/model-region";
import { AccordionService } from "./accordion.service";

export class AccordionPattern extends Pattern {
    componentRef!: ComponentRef<AccordionComponent>;
    closed$ = new Subject<any>();
    items: any[] = [];

    override render(region: ViewRegionComponent): void {
        // ignore rendered
        if (region.container.element.nativeElement.hasAttribute('rendered')) return;

        region.container.clear();

        this.componentRef = region.container.createComponent(AccordionComponent);
        this.componentRef.instance.items = this.items;

        this.componentRef.changeDetectorRef.detectChanges();

        // marked as rendered
        region.container.element.nativeElement.setAttribute('rendered', true);
        
        // handle events

    }

    forms: any = {};
    inputs: any[] = [];
    // TODO: change form id by parent
    override renderModel(region: ModelRegionDirective, data?: {} | undefined): void {
        // ignore rendered
        if (region.element.nativeElement.hasAttribute('rendered')) return;
        
        let id = region.element.nativeElement.getAttribute('id');
        if (id) {
            if (region.element.nativeElement.tagName.toLowerCase() === 'form') {
                const menu = { endpoint: id, name: id, subItem: this.inputs };

                this.inputs = [];
                this.forms[id] = menu;

                // get outer form
                const form = region.element.nativeElement.parentElement.closest('form');

                if (form) {
                    // inner form
                    const id = form.getAttribute('id');
                    let _menu = this.forms[id];
                    _menu.subItem.push(menu);
                } else {
                    // outmost level
                    this.items.unshift(menu);
                }
            } else {
                // get outer form id
                const form = region.element.nativeElement.parentElement.closest('form');

                if (form) {
                    // update id
                    const id = form.getAttribute('id') + '-' + region.element.nativeElement.id;
                    region.element.nativeElement.id = id;

                    // label
                    const label = region.element.nativeElement.parentElement.querySelector('label');

                    this.inputs.push({ endpoint: id, name: label.textContent });
                }
            }

            // marked as rendered
            region.element.nativeElement.setAttribute('rendered', true);
        }
    }

    override onDataChange(data: any = {}): void {
        // this.items = data.items;

        // this.componentRef.instance.items = this.items;
    }
}