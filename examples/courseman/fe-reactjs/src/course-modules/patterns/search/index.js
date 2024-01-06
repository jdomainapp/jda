import PatternConsumer from '../../../common/patterns/PatternConsumer'
import SearchProvider from '../../../common/patterns/autosearch/SearchProvider'
import AutoCompleteSearch from '../../../common/patterns/autosearch'
import SearchConsumer from './SearchConsumer'

export default class SearchFactory  {
    constructor() {
        
    }

    static createProviderConsumer(mainForm) {
        let provider = new SearchProvider()
        let consumer = new SearchConsumer({provider, mainForm})
        let pattern = new AutoCompleteSearch({searchLabel: mainForm.getSearchLabel(), searchFields: mainForm.getSearchFields(), content: mainForm.state.current.content})
        pattern.registerProvider(provider)

        return consumer
    }
}