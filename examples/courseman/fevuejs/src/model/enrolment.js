import Student from './student';
import Coursemodule from './course_module';
export default class Enrolment {
    constructor(id, internalMark, examMark, finalGrade, finalMark) {
        this.id = id
        this.student = new Student()
        this.courseModule = new Coursemodule()
        this.internalMark = internalMark
        this.examMark = examMark
        this.finalGrade = finalGrade
        this.finalMark = finalMark
    }
}