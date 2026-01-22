// -----------------------------
// DELETE MODAL
// -----------------------------
let deleteCallback = null;

export function openDeleteModal(message, onConfirm) {
    document.getElementById("confirmModalText").innerText = message;
    deleteCallback = onConfirm;

    new bootstrap.Modal(document.getElementById("confirmModal")).show();
}

document.getElementById("confirmYes").addEventListener("click", () => {
    if (deleteCallback) deleteCallback();
});


// -----------------------------
// EDIT MODAL
// -----------------------------
let saveCallback = null;

export function openEditModal(title, htmlForm, onSave) {
    document.getElementById("genericEditTitle").innerText = title;
    document.getElementById("genericEditBody").innerHTML = htmlForm;
    saveCallback = onSave;

    new bootstrap.Modal(document.getElementById("genericEditModal")).show();
}

document.getElementById("genericSaveBtn").addEventListener("click", () => {
    if (saveCallback) saveCallback();
});
