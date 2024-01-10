import courseModules from './MenuState'
import PatternConsumer from '../../../common/patterns/PatternConsumer'
import PatternProvider from '../../../common/patterns/PatternProvider'
import StructureConstructor from '../../../common/patterns/accordion/accordion'
import AccordionSearchableMenu from '../../../common/patterns/accordion'
import AccordionProvider from '../../../common/patterns/accordion/AccordionProvider'
import PatternFactory from '../../../common/patterns/PatternFactory'

export default class AccordionFactory extends PatternFactory  {
    constructor() {
        
    }

    // static createProviderConsumer(props = {}) {
    //     let provider = new AccordionProvider()
    //     let consumer = new PatternConsumer({provider, mainForm: props.mainForm, name: "accordion"})
    //     let state = new StructureConstructor(props.name === undefined ? "course_modules" : props.name, props.structure ? props.structure : courseModules)
    //     let pattern = new AccordionSearchableMenu({modules: state})
    //     pattern.registerProvider(provider)

    //     return consumer
    // }

    /* implement by subtype
      @effects result is an instance of PatternProvider
     */
    static createProvider(props) {
      return new AccordionProvider()
    }

    // implement by subtype
    static getPatternName() {
      return "accordion"
    }

    /*
      implement by subtype
      @effects result is an object, each property of which is a state variable
     */ 
    static initPatternState(props) {
      let moduleStruc = new StructureConstructor(props.name === undefined ? "course_modules" : props.name, props.structure ? props.structure : courseModules)
      return {modules: moduleStruc}
    }

    /*
      implement by subtype
      @requires state is an object, each property of which is a state variable
     */    
    static createPattern(state) {
      return new AccordionSearchableMenu(state)
    }

}