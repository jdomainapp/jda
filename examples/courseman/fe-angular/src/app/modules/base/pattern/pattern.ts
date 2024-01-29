import { ModelRegionDirective } from './model-region';
import { PatternProvider } from './pattern.provider';
import { ViewRegionComponent } from './view-region';

export abstract class Pattern {
    providers: PatternProvider[] = [];

    registerProvider(provider: PatternProvider) {
        provider.pattern = this;
        this.providers.push(provider);
    }

    abstract render(region: ViewRegionComponent): void;
    abstract onDataChange(data?: {}): void;
    
    renderModel(region: ModelRegionDirective, data?: {}): void { }

    onAction(action: string, event: any): void {
        this.providers.forEach(provider => {
            provider.onAction(action, event)
        });
    }

}