import Address from './address';
import StudentClass from './student_class';
export default class Student {
    constructor(id, name, gender, dob, email, address) {
        this.id = id
        this.name = name
        this.gender = gender
        this.dob = dob
        this.address = address
        this.studentClass = new StudentClass()
        this.email = email
    }

    setAddress(address) {
        if (address === undefined) {
            this.address = new Address();
            return;
        }

        this.address = address;
    }

    setStudentClass(studentClass) {
        if (studentClass === undefined) {
            this.studentClass = new StudentClass();
            return;
        }

        this.studentClass = studentClass;
    }
}