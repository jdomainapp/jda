export default class AddressForm {
    constructor() {
        this.hidId = true,
        this.hidName = true,
        this.hidStudent = true
    }

    setHidId(hidId) {
        this.hidId = hidId;
    }

    setHidName(hidName) {
        this.hidName = hidName;
    }

    setHidStudent(hidStudent) {
        this.hidStudent = hidStudent;
    }
}