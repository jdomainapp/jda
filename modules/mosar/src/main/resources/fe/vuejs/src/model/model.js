@loop{importLinkedModel}[[import @slot{{LinkedDomain}} from './@slot{{linked_domain}}';
]]loop{importLinkedModel}@
export default class @slot{{ModuleName}} {
    constructor(@loop{normalFieldParams}[[@slot{{fieldName}},]]loop{normalFieldParams}@) {
    @loop{initNormalFields}[[
        this.@slot{{fieldName}} = @slot{{fieldName}}]]loop{initNormalFields}@
    @loop{initLinkedOne2ManyFields}[[
        this.@slot{{fieldName}} = new @slot{{LinkedDomain}}()]]loop{initLinkedOne2ManyFields}@
    @loop{initLinkedOne2OneFields}[[
        this.@slot{{fieldName}} = @slot{{fieldName}}]]loop{initLinkedOne2OneFields}@
    }
@loop{setLinkedDomain}[[
    set@slot{{LinkedDomain}}(@slot{{fieldName}}) {
        if (@slot{{fieldName}} === undefined) {
            this.@slot{{fieldName}} = new @slot{{LinkedDomain}}();
            return;
        }

        this.@slot{{fieldName}} = @slot{{fieldName}};
    }]]loop{setLinkedDomain}@
}