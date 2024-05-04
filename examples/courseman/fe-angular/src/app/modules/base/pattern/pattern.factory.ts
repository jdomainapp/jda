import { Pattern } from "./pattern"
import { PatternConsumer } from "./pattern.consumer"
import { PatternProvider } from "./pattern.provider"

// as skeleton for factories
export abstract class PatternFactory {
  constructor() {
  }

  /*
    @effects initialise a pair of (PatternProvider, PatternConsumer) and a Pattern object, whose services
    are served by the pair to the target component.
    */
  static createProviderConsumer(data = {}) {
    let provider = PatternFactory.createProvider(data);
    let consumer = PatternFactory.createConsumer(provider!, data);
    let pattern = PatternFactory.createPattern(data);
    pattern!.registerProvider(provider!);

    return consumer;
  }

  /* implement by subtype
    @effects result is an instance of PatternProvider
    */
  static createProvider(data: any): PatternProvider | null {
    return null;
  }

  /* implement by subtype
    @effects create and return an instance of PatternConsumer that is paired with its specified provider
    */
  static createConsumer(provider: PatternProvider, data: any): PatternConsumer {
    return new PatternConsumer(provider, data);
  }

  /*
    implement by subtype
    @requires state is an object, each property of which is a state variable
  */
  static createPattern(data: any = {}): Pattern | null {
    return null;
  }
}
