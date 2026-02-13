axios.defaults.baseURL = window.APP.CONTEXT_PATH;
axios.defaults.headers.common["Content-Type"] = "application/json";

function showLoader() { $("#globalLoader").fadeIn(150); }
function hideLoader() { $("#globalLoader").fadeOut(150); }




// Optional: add interceptors for auth or logging
axios.interceptors.request.use((config) => { showLoader(); return config; });

// Add a response interceptor to handle errors globally
axios.interceptors.response.use(
    (response) => {
        hideLoader();
        return response;
    },
    (error) => {
        hideLoader();
        const status = error.response?.status;
        const message = error.response?.data?.message || "An unexpected error occurred";

        switch (status) {
            case 401:
                showToast("error", "Session expired. Please log in again.");
                setTimeout(() => (window.location.href = window.APP.CONTEXT_PATH + "/login"), 1000);
                break;
            case 403:
                showToast("error", "You don’t have permission to perform this action.");
                break;
            case 404:
                showToast("error", message);
                break;
            case 500:
                showToast("error", message);
                break;
            default:
                showToast("error", message);
        }

        return Promise.reject(error);
    }
);


const axiosClient = {
    async get(endpoint, params = {}) {
        const res = await axios.get(endpoint, { params });
        return unwrapResponse(res);
    },

    async post(endpoint, body = {}) {
        const res = await axios.post(endpoint, body);
        return unwrapResponse(res);
    },

    async put(endpoint, body = {}) {
        const res = await axios.put(endpoint, body);
        return unwrapResponse(res);
    },

    async delete(endpoint) {
        const res = await axios.delete(endpoint);
        return unwrapResponse(res);
    },

    async upload(endpoint, formData) {
        const res = await axios.post(endpoint, formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });
        return unwrapResponse(res);
    },
};

function unwrapResponse(res) {
    const payload = res.data;
    if (payload && typeof payload === "object" && "success" in payload) {
        if (!payload.success) {
            showToast("error", payload.message || "Operation failed");
            throw new Error(payload.message || "Operation failed");
        }
        return payload.data; // return the real data object
    }
    return payload; // fallback if API doesn't wrap in ApiResponse
}

window.axiosClient = axiosClient;