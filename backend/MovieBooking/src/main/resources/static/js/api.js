const API_BASE = "";

const Auth = {
  tokenKey: "mb_token",
  userKey: "mb_user",

  getToken() {
    return localStorage.getItem(this.tokenKey);
  },

  setSession(token, email) {
    localStorage.setItem(this.tokenKey, token);
    if (email) {
      localStorage.setItem(this.userKey, email);
    }
  },

  clear() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  },

  email() {
    return localStorage.getItem(this.userKey);
  },

  isLoggedIn() {
    return Boolean(this.getToken());
  }
};

async function api(path, options = {}) {
  const headers = Object.assign(
    { "Content-Type": "application/json" },
    options.headers || {}
  );

  if (options.auth) {
    const token = Auth.getToken();
    if (!token) {
      const err = new Error("Please log in first.");
      err.status = 401;
      throw err;
    }
    headers.Authorization = "Bearer " + token;
  }

  const response = await fetch(API_BASE + path, {
    method: options.method || "GET",
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined
  });

  const text = await response.text();
  let data = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch (_) {
      data = text;
    }
  }

  if (!response.ok) {
    const err = new Error(formatError(data, response.status));
    err.status = response.status;
    err.data = data;
    throw err;
  }

  return data;
}

function formatError(data, status) {
  if (!data) {
    return "Request failed (" + status + ")";
  }
  if (typeof data === "string") {
    return data;
  }
  if (data.error && data.errors) {
    const fields = Object.entries(data.errors)
      .map(([k, v]) => k + ": " + v)
      .join("; ");
    return data.error + (fields ? " — " + fields : "");
  }
  if (data.message) {
    return data.message;
  }
  if (data.error) {
    return data.error;
  }
  return "Request failed (" + status + ")";
}

function showMessage(el, text, type) {
  if (!el) return;
  el.textContent = text;
  el.className = "msg show " + (type || "ok");
}

function clearMessage(el) {
  if (!el) return;
  el.textContent = "";
  el.className = "msg";
}

function qs(name) {
  return new URLSearchParams(window.location.search).get(name);
}
