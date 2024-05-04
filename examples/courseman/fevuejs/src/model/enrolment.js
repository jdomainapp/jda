export default class Enrolment {
    constructor(
        id,
        student,
        courseModule,
        internalMark,
        examMark,
        finalGrade,
        finalMark,
        startDate,
        endDate
    ) {
        this.id = id;
        this.student = student;
        this.courseModule = courseModule;
        this.internalMark = internalMark;
        this.examMark = examMark;
        this.finalGrade = finalGrade;
        this.finalMark = finalMark;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    setStudent(student) {
        this.student = student;
    }
    setCourseModule(courseModule) {
        this.courseModule = courseModule;
    }
}
