export default class Enrolment {
    constructor() {

    }

    static formatResult(option) {
        return ["id",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}