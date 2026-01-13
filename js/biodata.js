document.getElementById('biodataForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const biodataInfo = {
        fullName: document.getElementById('fullName').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
        gender: document.getElementById('gender').value,
        address: document.getElementById('address').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        occupation: document.getElementById('occupation').value,
        nationality: document.getElementById('nationality').value
    };

    try {
        const response = await window.api.addBiodata(biodataInfo);
        const messageDiv = document.getElementById('message');

        if (response.success) {
            messageDiv.className = 'message success';
            messageDiv.textContent = response.message;
            document.getElementById('biodataForm').reset();
        } else {
            messageDiv.className = 'message error';
            messageDiv.textContent = response.message;
        }
    } catch (error) {
        const messageDiv = document.getElementById('message');
        messageDiv.className = 'message error';
        messageDiv.textContent = 'Error: ' + error.message;
    }
});

