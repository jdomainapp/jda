export default class Student {
    constructor(
        id,
        name,
        gender,
        dob,
        address,
        email,
        studentClass,
        enrolments
    ) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.studentClass = studentClass;
        this.enrolments = enrolments;
    }

    setAddress(address) {
        this.address = address;
    }
    setStudentClass(studentClass) {
        this.studentClass = studentClass;
    }
}
