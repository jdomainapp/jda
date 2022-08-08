export default class @slot{{ModuleName}}Form {
    constructor() {
        @loop{initHid}[[
        this.hid@slot{{FieldName}} = true;]]loop{initHid}@
    }

    @loop{setHidMethods}[[
    setHid@slot{{FieldName}}(hid@slot{{FieldName}}) {
        this.hid@slot{{FieldName}} = hid@slot{{FieldName}};
    }   ]]loop{setHidMethods}@

}