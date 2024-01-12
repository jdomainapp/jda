export class PatternConsumer {
    provider: any;

    constructor(props: any) { 
        this.provider = props.provider;
        this.provider.consumer = this;
        this.mainForm = props.mainForm;
    }

    onRenderRegion(region: string, props = {}) {
        return this.provider.onRenderRegion(region, props);
    }

    onModelRegion(region: string, props = {}) {
        return this.provider.onModelRegion(region, props);
    }

    onAction(action: string, props = {}) {

    }

    action(action: string, props = {}) {

    }

    onInitProviderModel() {

    }

    onInitProviderView() {

    }
    
}