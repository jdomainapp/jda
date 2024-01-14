import { PatternProvider } from "./pattern.provider";
import { ViewRegionComponent } from "./view-region";

export class PatternConsumer {
    provider: PatternProvider;
    host: any;

    constructor(provider: PatternProvider, host: any) { 
        this.provider = provider;
        this.provider.consumer = this;
        this.host = host;
    }

    onRenderRegion(region: ViewRegionComponent, data = {}) {
        return this.provider.onRenderRegion(region, data);
    }

    onModelRegion(region: string, data = {}) {
        return this.provider.onModelRegion(region, data);
    }

    onDataChange(data: any = {}) {
        this.provider.onDataChange(data);
    }

    onAction(action: string, data = {}) {
        
    }

    action(action: string, data = {}) {

    }

    onInitProviderModel() {

    }

    onInitProviderView() {

    }
    
}