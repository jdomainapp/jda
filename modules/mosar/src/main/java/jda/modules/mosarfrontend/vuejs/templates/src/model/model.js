@loop{importLinkedModel}[[import @slot{{LinkedDomain}} from './@slot{{linked_domain}}';
]]loop{importLinkedModel}@
export default class @slot{{ModuleName}} {
    constructor(@loop{normalFieldParams}[[@slot{{fieldName}},]]loop{normalFieldParams}@) {
    @loop{initNormalFields}[[
        this.@slot{{fieldName}} = @slot{{fieldName}}]]loop{initNormalFields}@
    @loop{initLinkedFields}[[
        this.@slot{{fieldName}} = new @slot{{LinkedDomain}}()]]loop{initLinkedFields}@
    }

@loop{setLinkedDomain}[[
    set@slot{{LinkedDomain}}(@slot{{fieldName}}) {
        if (@slot{{fieldName}} === undefined) {
            this.@slot{{fieldName}} = new @slot{{LinkedDomain}}();
            return;
        }

        this.@slot{{fieldName}} = @slot{{fieldName}};
    }
]]loop{setLinkedDomain}@
}