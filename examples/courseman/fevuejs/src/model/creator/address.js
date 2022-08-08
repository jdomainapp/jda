class Address {
    constructor(id, name, student) {
        this.id = id;
        this.name = name;
        this.student = student;
    }
}

const AddressBuilder = function () {
    let id;
    let name;
    let student;

    return {
        setId: function (id) {
            this.id = id;
            return this;
        },

        setName: function (name) {
            this.name = name;
            return this;
        },

        setStudent: function (student) {
            this.student = student;
            return this;
        },

        build: function () {
            return new Address(id, name, student);
        }
    };
};

export {
    AddressBuilder,
    Address
}