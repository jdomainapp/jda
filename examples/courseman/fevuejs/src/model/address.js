export default class Address {
    constructor(id, name, student) {
        this.id = id;
        this.name = name;
        this.student = student;
    }

    setStudent(student) {
        this.student = student;
    }
}
