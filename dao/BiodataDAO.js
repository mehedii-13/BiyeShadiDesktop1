const Biodata = require('../models/Biodata');

class BiodataDAO {
    constructor(db) {
        this.db = db;
    }

    create(biodata) {
        const query = `INSERT INTO biodata (user_id, full_name, date_of_birth, gender, address, phone, email, occupation, nationality) 
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;
        return this.db.run(query, [
            biodata.userId,
            biodata.fullName,
            biodata.dateOfBirth,
            biodata.gender,
            biodata.address,
            biodata.phone,
            biodata.email,
            biodata.occupation,
            biodata.nationality
        ]);
    }

    getByUserId(userId) {
        const query = `SELECT * FROM biodata WHERE user_id = ?`;
        return this.db.get(query, [userId]);
    }

    update(biodata) {
        const query = `UPDATE biodata SET full_name = ?, date_of_birth = ?, gender = ?, address = ?, 
                       phone = ?, email = ?, occupation = ?, nationality = ? WHERE id = ?`;
        return this.db.run(query, [
            biodata.fullName,
            biodata.dateOfBirth,
            biodata.gender,
            biodata.address,
            biodata.phone,
            biodata.email,
            biodata.occupation,
            biodata.nationality,
            biodata.id
        ]);
    }
}

module.exports = BiodataDAO;

