import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { PatternProviderComponent } from 'src/app/pattern/pattern.provider';

@Component({
    selector: 'accordion-provider',
    template: '<ng-content></ng-content>',
})
export class AccordionProvider {
    
    onRenderRegion(region: string): void {
        
    }

    onAction(action: string, data: any): void {

    }
}