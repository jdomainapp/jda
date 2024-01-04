import courseModules from 'MenuState'
import PatternConsumer from '../../../common/patterns/PatternConsumer'
import PatternProvider from '../../../common/patterns/PatternProvider'
import StructureConstructor from '../../../common/patterns/accordion/accordion'
import AccordionSearchableMenu from '../../../common/patterns/accordion'

export default class AccordionConsumerMain  {
    constructor() {
        
    }

    init(mainForm) {
        let provider = new AccordionProvider()
        let consumer = new PatternConsumer(provider)
        let state = new StructureConstructor(courseModules)
        let pattern = new AccordionSearchableMenu({modules: state, controlling: mainForm})
        pattern.registerProvider(provider)

        return consumer

        // initialise p: AccordionProvider
        // initialise c: AccordionConsumer(p) // bind provider-consumer together
        // initialise courseModules...menu state
        // initialise state:...MenuState using StructureConstructor(courseModules)
        // initialise menu: AccordionSearchableMenu(state)
        // register...
        // call menu.registerProvider(p)
    }
}