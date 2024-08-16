import courseModules from './MenuState'
import PatternConsumer from '../../../common/patterns/PatternConsumer'
import StructureConstructor from '../../../common/patterns/accordion/accordion'
import AccordionSearchableMenu from '../../../common/patterns/accordion'
import AccordionProvider from '../../../common/patterns/accordion/AccordionProvider'

import PatternFactory from "../../../common/patterns/PatternFactory";

export default class AccordionFactory extends PatternFactory {

    static createProvider(props) {
        return new AccordionProvider()
    }

    static createConsumer(props) {
        return new PatternConsumer({provider: props.provider, name: "accordion"})
    }

    static initPatternState(props) {
        return {
            modules: new StructureConstructor(props.name === undefined ? "course_modules" : props.name, props.structure ? props.structure : courseModules)
        }
    }

    static createPattern(state) {
        return new AccordionSearchableMenu(state)
    }
}