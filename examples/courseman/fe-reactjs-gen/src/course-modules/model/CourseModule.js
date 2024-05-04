export default class CourseModule {
    constructor() {

    }

    static formatResult(option) {
        return ["code","name","description",].reduce((format,key)=> `${format}  ${option[key]}`,"")
    }
}