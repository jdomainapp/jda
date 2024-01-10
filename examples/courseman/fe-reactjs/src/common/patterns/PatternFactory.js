import PatternConsumer from './PatternConsumer'
import PatternProvider from './PatternProvider'

export default class PatternFactory  {
    constructor() {
        
    }

    static createProviderConsumer(props = {}) {
        let provider = this.createProvider(props)
        let consumer = this.createConsumer(provider, props)
        let state = this.initPatternState(props)
        let pattern = this.createPattern(state)
        pattern.registerProvider(provider)

        return consumer
    }

    /* implement by subtype
      @effects result is an instance of PatternProvider
     */
    static createProvider(props) {

    }

    /* implement by subtype
      @effects create and return an instance of PatternConsumer that is paired with its specified provider
     */
    static createConsumer(provider, props) {
      return new PatternConsumer({provider, mainForm: props.mainForm, name: this.getPatternName()})
    }

    // implement by subtype
    static getPatternName() {

    }

    /*
      implement by subtype
      @effects result is an object, each property of which is a state variable
     */    
    static initPatternState(props) {

    }

    /*
      implement by subtype
      @requires state is an object, each property of which is a state variable
     */
    static createPattern(state) {

    }
}