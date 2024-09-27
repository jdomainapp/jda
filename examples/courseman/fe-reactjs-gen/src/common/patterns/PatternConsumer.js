// Pattern consumer

export default class PatternConsumer {
    constructor(props) {
        this.name = props.name ? props.name : ""
        this.provider = props.provider
        props.provider.consumer = this
        this.mainForm = props.mainForm
    }

    onRenderRegion(region, props={}) {
        return this.provider.onRenderRegion(region, props)
    }

    onModelRegion(region, props={}) {
        return this.provider.onModelRegion(region, props)
    }

    onAction(action, props={}) {

    }

    action(action, props={}) {
        
    }

    onInitProviderModel() {

    }
    
    onInitProviderView() {
        
    }
}