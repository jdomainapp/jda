export default class @slot{{ModuleName}} {
    constructor() {

    }

    static formatResult(option) {
        return [@loop{searchKeys}[["@slot{{key}}",]]loop{searchKeys}@].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}