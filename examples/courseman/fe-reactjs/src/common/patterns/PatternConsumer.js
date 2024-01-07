// Pattern consumer

export default class PatternConsumer {
    constructor(props) {
        this.name = props.name ? props.name : ""
        this.provider = props.provider
        props.provider.consumer = this
    }

    onRenderRegion(region, mainForm) {
        return this.provider.onRenderRegion(region, mainForm)
    }

    onModelRegion(region, mainForm) {
        return this.provider.onModelRegion(region, mainForm)
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