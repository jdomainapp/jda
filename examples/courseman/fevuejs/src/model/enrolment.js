export default class Enrolment {
    constructor(
        id,
        student,
        courseModule,
        internalMark,
        examMark,
        finalGrade,
        finalMark
    ) {
        this.id = id;
        this.student = student;
        this.courseModule = courseModule;
        this.internalMark = internalMark;
        this.examMark = examMark;
        this.finalGrade = finalGrade;
        this.finalMark = finalMark;
    }

    setStudent(student) {
        this.student = student;
    }
    setCourseModule(courseModule) {
        this.courseModule = courseModule;
    }
}
