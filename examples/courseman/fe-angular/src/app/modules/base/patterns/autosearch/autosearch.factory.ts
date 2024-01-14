import { AutoSearchConsumer } from "./autosearch.consumer";
import { AutoSearchPattern } from "./autosearch.pattern";
import { AutoSearchProvider } from "./autosearch.provider"

export class AutoSearchFactory {

    static createProviderConsumer(data: any = {}): AutoSearchConsumer {
        let provider = new AutoSearchProvider();
        let consumer = new AutoSearchConsumer(provider, data.host);
        let pattern = new AutoSearchPattern(data.host.items);
        pattern!.registerProvider(provider);

        return consumer;    
    }

}