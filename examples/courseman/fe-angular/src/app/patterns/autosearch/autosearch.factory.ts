import { AutoSearchConsumer } from "./autosearch.consumer";
import { AutoSearchProvider } from "./autosearch.provider"

export class AutoSearchFactory {
    static createProviderConsumer(props = {}) {
        let provider = new AutoSearchProvider();
        let consumer = new AutoSearchConsumer({ provider, mainForm: props.mainForm })
        let pattern = new AutoCompleteSearch({
            formatResult: Students.formatResult,
            searchFields: [],
            content: props.mainForm.state.current.content
        })
        pattern.registerProvider(provider)

        return consumer
    }      
}