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
        region.container.clear();
        
        this.componentRef = region.container.createComponent(AccordionComponent);
        this.componentRef.instance.items = this.items;
        this.componentRef.changeDetectorRef.detectChanges();

        // handle events

    }

    forms: any = {};

    // TODO: change form id by parent
    override renderModel(region: ModelRegionDirective, data?: {} | undefined): void {
        console.log('model')
        const nativeElement = region.element.nativeElement;

        let id = nativeElement.getAttribute('id');
        if (id) {
            // get outer form id
            const form = nativeElement.parentElement.closest('form');
            
            // update id if not rendered
            if (form && !nativeElement.hasAttribute('rendered')) {
                id = form.getAttribute('id') + '-' + nativeElement.id;
                nativeElement.id = id;
            }
            
            // if form element 
            if (nativeElement.tagName.toLowerCase() === 'form') {
                const item = { endpoint: id, name: id, subItem: [] };
                this.forms[id] = item;

                // if outer form
                if (form) {
                    let _item = this.forms[form.getAttribute('id')];
                    _item.subItem.push(item);
                } else {
                    // outmost level
                    this.items.push(item);
                }
            } else { // forminput element
                // label
                const label = nativeElement.parentElement.querySelector('label');

                const _item = this.forms[form.getAttribute('id')];
                _item.subItem.push({ endpoint: id, name: label.textContent });
            }
            
            // marked as rendered
            nativeElement.setAttribute('rendered', true);
        }
    }

    override onDataChange(data: any = {}): void {
        // this.items = data.items;
        // this.componentRef.instance.items = this.items;
    }
}