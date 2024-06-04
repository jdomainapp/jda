export default class Addresses {
    constructor() {

    }

    static formatResult(option) {
        return ["name",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}