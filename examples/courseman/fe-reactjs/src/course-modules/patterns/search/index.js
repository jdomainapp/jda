// import PatternConsumer from '../../../common/patterns/PatternConsumer'
import SearchProvider from '../../../common/patterns/autosearch/SearchProvider'
import PatternFactory from '../../../common/patterns/PatternFactory'

import AutoCompleteSearch from '../../../common/patterns/autosearch'
import SearchConsumer from './SearchConsumer'
import CourseModuleModel from '../../model/CourseModule'

export default class SearchFactory  extends PatternFactory {
    constructor() {
        
    }

    // static createProviderConsumer(props = {}) {
    //     let provider = new SearchProvider()
    //     let consumer = new SearchConsumer({provider, mainForm: props.mainForm})
    //     let pattern = new AutoCompleteSearch({
    //         formatResult: CourseModuleModel.formatResult, 
    //         searchFields: props.mainForm.getSearchFields(), 
    //         content: props.mainForm.state.current.content
    //     })
    //     pattern.registerProvider(provider)

    //     return consumer
    // }

    // implement by subtype
    static createProvider(props) {
      return new SearchProvider()
    }

    /* implement by subtype
      @effects create and return an instance of PatternConsumer that is paired with its specified provider
     */
    static createConsumer(provider, props) {
      return new SearchConsumer({provider, mainForm: props.mainForm})
    }

    // implement by subtype
    static getPatternName() {
      return "autosearch"
    }

    /*
      implement by subtype
      @effects result is an object, each property of which is a state variable
     */ 
    static initPatternState(props) {
      let state = {
              formatResult: CourseModuleModel.formatResult, 
              searchFields: props.mainForm.getSearchFields(), 
              content: props.mainForm.state.current.content
          }
      return state
    }

    /*
      implement by subtype
      @requires state is an object, each property of which is a state variable
     */    
    static createPattern(state) {
      return new AutoCompleteSearch(state)
    }
}