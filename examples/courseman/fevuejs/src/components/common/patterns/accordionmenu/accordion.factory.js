import AccordionConsumer from "./accordion.consumer";
import AccordionPattern from "./accordion.pattern";
import AccordionProvider from "./accordion.provider";

export default class AccordionFactory {
    static createProviderConsumer(data = {}) {
        data;

        let provider = new AccordionProvider();
        let consumer = new AccordionConsumer(provider, data.host);
        let pattern = new AccordionPattern();
        pattern.registerProvider(provider);

        return consumer;
    }
}
