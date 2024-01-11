export default class PatternFactory {
    constructor() {

    }

    createProvider(props = {}) {
        return null
    }

    createConsumer(props = {}) {
        return null
    }

    createPattern(props = {}) {
        return null
    }

    static createProviderConsumer(props) {
        let provider = this.createProvider(props)
        let consumer = this.createConsumer({provider, ...props})
        let pattern = this.createPattern(props)
        pattern.registerProvider(provider)

        return consumer
    }
}