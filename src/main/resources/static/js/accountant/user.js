$(document).ready(function () {
    const orgId = $('#globalOrgId').val() || window.APP.ORG_ID;

    $('#accountantForm').on('submit', function (e) {
        e.preventDefault();
        const email = $('#email').val();
        const password = $('#password').val();

        createAccountantUser(email, password);
    })

    async function createAccountantUser(email, password) {
        try {
            const response = await axios.post(`/api/accountant/${orgId}/create`, { email, password });
            showToast('success', "Accountant user created successfully!");
            $('#accountantForm').trigger('reset');
            setTimeout(() => {
                window.location.replace(`/org/dashboard`);
            }, 1000);
        } catch (err) {
            showToast('error', "Accountant user creation failed!");
        }
    }

})