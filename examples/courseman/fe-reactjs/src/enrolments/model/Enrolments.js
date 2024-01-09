export default class EnrolmentsModel {
    constructor() {

    }

    static formatResult(option) {
        return option.student.name + " " + option.courseModule.name
    }
}