/* eslint-disable no-unused-vars */
export default class Pattern {
    providers = [];

    registerProvider(provider) {
        provider.pattern = this;
        this.providers.push(provider);
    }

    render(region) {}
    onDataChange(data) {}

    renderModel(region, data) {}
    
    onAction(action, event) {
        this.providers.forEach((provider) => {
            provider.onAction(action, event);
        });
    }
}