export default class Enrolment {
    constructor() {
        this.hidId = true
        this.hidStudent = true
        this.hidCoursemodule = true
        this.hidInternalmark = true
        this.hidExammark = true
        this.hidFinalgrade = true
        this.hidFinalmark = true
    }

    setHidId(hidId) {
        this.hidId = hidId;
    }

    setHidStudent(hidStudent) {
        this.hidStudent = hidStudent;
    }

    setHidCoursemodule(hidCoursemodule) {
        this.hidCoursemodule = hidCoursemodule;
    }

    setHidInternalmark(hidInternalmark) {
        this.hidInternalmark = hidInternalmark;
    }

    setHidExammark(hidExammark) {
        this.hidExammark = hidExammark;
    }

    setHidFinalgrade(hidFinalgrade) {
        this.hidFinalgrade = hidFinalgrade;
    }

    setHidFinalmark(hidFinalmark) {
        this.hidFinalmark = hidFinalmark;
    }
}