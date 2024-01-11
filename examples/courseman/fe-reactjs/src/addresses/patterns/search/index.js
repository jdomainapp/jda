import PatternConsumer from '../../../common/patterns/PatternConsumer'
import SearchProvider from '../../../common/patterns/autosearch/SearchProvider'
import AutoCompleteSearch from '../../../common/patterns/autosearch'
import SearchConsumer from './SearchConsumer'
import Addresses from '../../model/Addresses'
import PatternFactory from '../../../common/patterns/PatternFactory'

export default class SearchFactory extends PatternFactory {

    static createProvider(props) {
        return new SearchProvider()
    }

    static createConsumer(props) {
        return new SearchConsumer({provider: props.provider, mainForm: props.mainForm})
    }

    static createPattern(props) {
        return new AutoCompleteSearch({
            formatResult: Addresses.formatResult, 
            searchFields: [], 
            content: props.mainForm.state.current.content
        })
    }
}