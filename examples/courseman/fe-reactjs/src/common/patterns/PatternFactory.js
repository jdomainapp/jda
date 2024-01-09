import PatternConsumer from './PatternConsumer'
import PatternProvider from './PatternProvider'

export default class PatternFactory  {
    constructor() {
        
    }

    static createProviderConsumer(props = {}) {
        let provider = createProvider()
        let consumer = new PatternConsumer({provider, mainForm: props.mainForm, name: getPatternName()})
        let state = initPatternState()
        let pattern = createPattern(state)
        pattern.registerProvider(provider)

        return consumer
    }

    // implement by subtype
    static createProvider() {

    }

    // implement by subtype
    static getPatternName() {

    }

    // implement by subtype
    static initPatternState() {

    }

    // implement by subtype
    static createPattern(state) {

    }
}