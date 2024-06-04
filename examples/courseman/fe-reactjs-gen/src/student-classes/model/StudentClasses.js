export default class StudentClasses {
    constructor() {

    }

    static formatResult(option) {
        return ["name",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}