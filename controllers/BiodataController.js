const BiodataDAO = require('../dao/BiodataDAO');
const Biodata = require('../models/Biodata');

class BiodataController {
    constructor(db) {
        this.biodataDAO = new BiodataDAO(db);
    }

    async addBiodata(userId, biodataInfo) {
        try {
            const biodata = new Biodata(
                null,
                userId,
                biodataInfo.fullName,
                biodataInfo.dateOfBirth,
                biodataInfo.gender,
                biodataInfo.address,
                biodataInfo.phone,
                biodataInfo.email,
                biodataInfo.occupation,
                biodataInfo.nationality
            );

            await this.biodataDAO.create(biodata);
            return { success: true, message: 'Biodata added successfully' };
        } catch (error) {
            return { success: false, message: error.message };
        }
    }

    async getBiodata(userId) {
        try {
            const biodata = await this.biodataDAO.getByUserId(userId);
            return { success: true, data: biodata };
        } catch (error) {
            return { success: false, message: error.message };
        }
    }

    async updateBiodata(biodataId, biodataInfo) {
        try {
            const biodata = new Biodata(
                biodataId,
                biodataInfo.userId,
                biodataInfo.fullName,
                biodataInfo.dateOfBirth,
                biodataInfo.gender,
                biodataInfo.address,
                biodataInfo.phone,
                biodataInfo.email,
                biodataInfo.occupation,
                biodataInfo.nationality
            );

            await this.biodataDAO.update(biodata);
            return { success: true, message: 'Biodata updated successfully' };
        } catch (error) {
            return { success: false, message: error.message };
        }
    }
}

module.exports = BiodataController;

