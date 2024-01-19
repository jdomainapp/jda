export default class Address {
    constructor() {

    }

    static formatResult(option) {
        return ["name",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}