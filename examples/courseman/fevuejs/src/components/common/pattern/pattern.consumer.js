/* eslint-disable no-unused-vars */
export default class PatternConsumer {
    provider;
    host;

    constructor(provider, host) {
        this.provider = provider;
        this.provider.consumer = this;
        this.host = host;
    }

    onRenderRegion(region, data = {}) {
        return this.provider.onRenderRegion(region, data);
    }

    onModelRegion(region, data = {}) {
        return this.provider.onModelRegion(region, data);
    }

    onDataChange(data = {}) {
        this.provider.onDataChange(data);
    }

    onAction(action, data = {}) {}

    action(action, data = {}) {}

    onInitProviderModel() {}

    onInitProviderView() {}
}
