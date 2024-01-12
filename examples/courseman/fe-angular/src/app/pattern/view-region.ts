import {
    Component,
    ComponentRef,
    ContentChild,
    TemplateRef,
    ViewContainerRef
} from '@angular/core';

@Component({
    selector: 'ViewRegion',
    template: '<ng-content></ng-content>'
})
export class ViewRegionComponent {
    name!: string;
    
    @ContentChild(TemplateRef) templateRef!: TemplateRef<any>;

    constructor(
        private container: ViewContainerRef,
    ) { }
    
    ngAfterViewInit() {
        
        // const comp: ComponentRef<AccordionComponent> = this.container.createComponent(AccordionComponent);
        // comp.instance.ref = this.templateRef;
        
    }
}
