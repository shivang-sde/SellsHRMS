document.addEventListener("DOMContentLoaded", () => {
    loadDocuments();
    document.getElementById("btnSaveDoc").addEventListener("click", saveDocument);
});

function empId() {
    return document.getElementById("empId").value;
}

function val(id) {
    return document.getElementById(id).value;
}

/* -------------------------
   SAVE DOCUMENT
------------------------- */
async function saveDocument() {

    const payload = {
        employeeId: empId(),
        documentType: val("documentType"),
        externalUrl: val("externalUrl"),
        removeFile: val("removeFile") === "true"
    };

    const res = await fetch("/api/employee/documents/link", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert("Document saved");
        loadDocuments();
    } else {
        alert("Error saving document");
    }
}

/* -------------------------
   LOAD DOCUMENT LIST
------------------------- */
async function loadDocuments() {

    const res = await fetch(`/api/employee/documents/${empId()}`);
    const data = await res.json();

    const body = document.getElementById("docTableBody");
    body.innerHTML = "";

    data.forEach(d => {
        body.innerHTML += `
        <tr>
            <td>${d.documentType}</td>
            <td><a href="${d.externalUrl}" target="_blank">Open</a></td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteDocument(${d.id})">Delete</button></td>
        </tr>
        `;
    });
}

/* -------------------------
   DELETE DOCUMENT
------------------------- */
async function deleteDocument(id) {

    if (!confirm("Delete this document?")) return;

    const res = await fetch(`/api/employee/documents/${id}`, {
        method: "DELETE"
    });

    if (res.ok) {
        loadDocuments();
    } else {
        alert("Error deleting document");
    }
}
