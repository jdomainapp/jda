export default class StudentForm {
    constructor() {
        this.hidId = true,
        this.hidName = true,
        this.hidGender = true,
        this.hidDob = true,
        this.hidAddress = true,
        this.hidEmail = true
    }

    setHidId(hidId) {
        this.hidId = hidId;
    }

    setHidName(hidName) {
        this.hidName = hidName;
    }

    setHidGender(hidGender) {
        this.hidGender = hidGender;
    }

    setHidDob(hidDob) {
        this.hidDob = hidDob;
    }

    setHidAddress(hidAddress) {
        this.hidAddress = hidAddress;
    }

    setHidEmail(hidEmail) {
        this.hidEmail = hidEmail;
    }
}