export default class Student {
    constructor() {

    }

    static formatResult(option) {
        return ["id",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}