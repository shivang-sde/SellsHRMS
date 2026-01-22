document.addEventListener("DOMContentLoaded", () => {
    loadBankList();
    document.getElementById("btnSaveBank").addEventListener("click", saveBank);
});

function empId() {
    return document.getElementById("empId").value;
}

function val(id) {
    return document.getElementById(id).value;
}

/* -------------------------
   SAVE BANK ACCOUNT
------------------------- */
async function saveBank() {

    const payload = {
        employeeId: empId(),
        bankName: val("bankName"),
        accountNumber: val("accountNumber"),
        ifscCode: val("ifscCode"),
        branch: val("branch"),
        isPrimaryAccount: val("isPrimaryAccount") === "true"
    };

    const res = await fetch("/api/employee/bank", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert("Bank account added");
        loadBankList();
    } else {
        alert("Error adding bank account");
    }
}

/* -------------------------
   LIST BANK ACCOUNTS
------------------------- */
async function loadBankList() {

    const res = await fetch(`/api/employee/bank/${empId()}`);
    const list = await res.json();

    const body = document.getElementById("bankTableBody");
    body.innerHTML = "";

    list.forEach(b => {
        body.innerHTML += `
        <tr>
            <td>${b.bankName}</td>
            <td>${b.accountNumber}</td>
            <td>${b.ifscCode}</td>
            <td>${b.branch}</td>
            <td>${b.primaryAccount ? "Yes" : "No"}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deleteBank(${b.id})">Delete</button>
            </td>
        </tr>
        `;
    });
}

/* -------------------------
   DELETE BANK ACCOUNT
------------------------- */
async function deleteBank(id) {

    if (!confirm("Delete bank account?")) return;

    const res = await fetch(`/api/employee/bank/${id}`, {
        method: "DELETE"
    });

    if (res.ok) {
        loadBankList();
    } else {
        alert("Failed to delete");
    }
}
