import @slot{{moduleNames}} from './MenuState'
import PatternConsumer from '../../../common/patterns/PatternConsumer'
import PatternProvider from '../../../common/patterns/PatternProvider'
import StructureConstructor from '../../../common/patterns/accordion/accordion'
import AccordionSearchableMenu from '../../../common/patterns/accordion'
import AccordionProvider from '../../../common/patterns/accordion/AccordionProvider'

export default class AccordionFactory  {
    constructor() {

    }

    static createProviderConsumer(props = {}) {
        let provider = new AccordionProvider()
        let consumer = new PatternConsumer({provider, mainForm: props.mainForm, name: "accordion"})

        let state = new StructureConstructor(props.name === undefined ? "@slot{{module_names}}" : props.name, props.structure ? props.structure : @slot{{moduleNames}})

        let pattern = new AccordionSearchableMenu({modules: state})
        pattern.registerProvider(provider)

        return consumer
    }
}