export default class @slot{{ModuleName}} {
    constructor(@if{typeRequired}((type,))if{typeRequired}@ @loop{normalFieldParams}[[@slot{{fieldName}}, ]]loop{normalFieldParams}@) {
    @if{genericModule}((    this.type = type))if{genericModule}@@loop{initNormalFields}[[
        this.@slot{{fieldName}} = @slot{{fieldName}}]]loop{initNormalFields}@
    }

@loop{setLinkedDomain}[[
    set@slot{{LinkedDomain}}(@slot{{fieldName}}) {
        this.@slot{{fieldName}} = @slot{{fieldName}};
    }]]loop{setLinkedDomain}@
}