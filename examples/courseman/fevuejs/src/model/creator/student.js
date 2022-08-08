class Student {
    constructor(id, name, gender, dob, email, address, studentClass) {
        this.id = id
        this.name = name
        this.gender = gender
        this.dob = dob
        this.email = email
        this.address = address
        this.studentClass = studentClass
    }
}

const StudentBuilder = function () {
    let id
    let name
    let gender
    let dob
    let email
    let address
    let studentClass

    return {
        setId: function (id) {
            this.id = id;
            return this;
        },

        setName: function (name) {
            this.name = name;
            return this;
        },

        setGender: function (gender) {
            this.gender = gender;
            return this;
        },

        setDob: function (dob) {
            this.dob = dob;
            return this;
        },

        setEmail: function (email) {
            this.email = email;
            return this;
        },

        setAddress: function (address) {
            this.address = address;
            return this;
        },

        setStudentClass: function (studentClass) {
            this.studentClass = studentClass;
            return this;
        },

        build: function () {
            return new Student(id, name, gender, dob, email, address, studentClass);
        }
    };
};

export {
    StudentBuilder,
    Student
}