/* eslint-disable no-unused-vars */
import Pattern from "./pattern";
import PatternProvider from "./pattern.provider";
import PatternConsumer from "./pattern.consumer";

// abstract class
export default class PatternFactory {
    constructor() {}

    static createProviderConsumer(data = {}) {
        let provider = PatternFactory.createProvider(data);
        let consumer = PatternFactory.createConsumer(provider, data);
        let pattern = PatternFactory.createPattern(data);
        pattern.registerProvider(provider);

        return consumer;
    }

    static createProvider(data) {
        return null;
    }

    static createConsumer(provider, data) {
        return new PatternConsumer(provider, data);
    }

    static createPattern(data = {}) {
        return null;
    }
}
