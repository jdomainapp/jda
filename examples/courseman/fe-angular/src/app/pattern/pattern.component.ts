import {
    Component
} from '@angular/core';

@Component({
    selector: 'PatternComponent',
    template: '<ng-content></ng-content>'
})
export class PatternComponent {
    providers: any[] = [];

    registerProvider(provider: any) {
        provider.pattern = this;
        this.providers.push(provider);
    }

    render() {
        
    }
}
