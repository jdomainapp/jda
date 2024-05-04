import AutoSearchProvider from './autosearch.provider';
import AutoSearchConsumer from './autosearch.consumer';
import AutoSearchPattern from './autosearch.pattern';

export default class AutoSearchFactory {
    static createProviderConsumer(data = {}) {
        let provider = new AutoSearchProvider();
        let consumer = new AutoSearchConsumer(provider, data.host);
        let pattern = new AutoSearchPattern(data.host.items);
        pattern.registerProvider(provider);

        return consumer;    
    }
}