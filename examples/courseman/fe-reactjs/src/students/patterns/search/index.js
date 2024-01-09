import PatternConsumer from '../../../common/patterns/PatternConsumer'
import SearchProvider from '../../../common/patterns/autosearch/SearchProvider'
import AutoCompleteSearch from '../../../common/patterns/autosearch'
import SearchConsumer from './SearchConsumer'
import StudentsModel from '../../model/Students'

export default class SearchFactory  {
    constructor() {
        
    }

    static createProviderConsumer(props = {}) {
        let provider = new SearchProvider()
        let consumer = new SearchConsumer({provider, mainForm: props.mainForm})
        let pattern = new AutoCompleteSearch({
            formatResult: StudentsModel.formatResult, 
            searchFields: [], 
            content: props.mainForm.state.current.content
        })
        pattern.registerProvider(provider)

        return consumer
    }
}