// s: PatternConsumer
// todo: ducmle

export default class PatternConsumer {
    constructor(provider) {
        this.provider = provider
        provider.consumer = this
    }

    onRenderRegion(region, mainForm) {
        return this.provider.onRenderRegion(region, mainForm)
    }

    onInitProviderModel() {

    }
    
    onInitProviderView() {
        
    }
}