function exportToExcel(data, filename = "export.xlsx") {
    if (!data || !data.length) {
        showToast("info", "No data available for export");
        return;
    }

    const headers = Object.keys(data[0]);
    const rows = data.map((obj) => headers.map((h) => obj[h] ?? ""));

    const csvContent = [
        headers.join(","),
        ...rows.map((r) => r.join(",")),
    ].join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);

    showToast("success", "Excel file exported successfully");
}

function exportToPDF(data, filename = "export.pdf") {
    const doc = new window.jspdf.jsPDF();
    const headers = Object.keys(data[0]);
    const rows = data.map((obj) => headers.map((h) => obj[h] ?? ""));

    doc.autoTable({
        head: [headers],
        body: rows,
        styles: { fontSize: 8 },
    });

    doc.save(filename);
    showToast("success", "PDF exported successfully");
}