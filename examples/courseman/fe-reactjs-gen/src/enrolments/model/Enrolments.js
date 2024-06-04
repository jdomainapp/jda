export default class Enrolments {
    constructor() {

    }

    static formatResult(option) {
        return ["id",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}