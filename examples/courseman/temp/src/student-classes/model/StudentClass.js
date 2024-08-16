export default class StudentClass {
    constructor() {

    }

    static formatResult(option) {
        return ["name",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}