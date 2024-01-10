export default class Enrolments {
    constructor() {

    }

    static formatResult(option) {
        return option.student.name + " " + option.courseModule.name
    }
}