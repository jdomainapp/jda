// Pattern consumer

export default class PatternConsumer {
    constructor(props) {
        this.name = props.name ? props.name : ""
        this.provider = props.provider
        props.provider.consumer = this
    }

    onRenderRegion(region, mainForm, props = {}) {
        return this.provider.onRenderRegion(region, mainForm, props)
    }

    onModelRegion(region, mainForm, props = {}) {
        return this.provider.onModelRegion(region, mainForm, props)
    }

    onAction() {

    }

    action() {

    }

    onInitProviderModel() {

    }
    
    onInitProviderView() {
        
    }
}