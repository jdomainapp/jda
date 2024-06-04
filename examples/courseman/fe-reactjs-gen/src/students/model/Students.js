export default class Students {
    constructor() {

    }

    static formatResult(option) {
        return ["id",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}