class Biodata {
    constructor(id, userId, fullName, dateOfBirth, gender, address, phone, email, occupation, nationality) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.occupation = occupation;
        this.nationality = nationality;
    }
}

module.exports = Biodata;

