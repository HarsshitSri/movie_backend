const API_BASE = "";

const Auth = {
  tokenKey: "mb_token",
  userKey: "mb_user",
  roleKey: "mb_role",
  userIdKey: "mb_user_id",

  getToken() {
    return localStorage.getItem(this.tokenKey);
  },

  setSession(auth) {
    localStorage.setItem(this.tokenKey, auth.token);
    if (auth.email) {
      localStorage.setItem(this.userKey, auth.email);
    }
    if (auth.role) {
      localStorage.setItem(this.roleKey, auth.role);
    }
    if (auth.userId != null) {
      localStorage.setItem(this.userIdKey, String(auth.userId));
    }
  },

  clear() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.userIdKey);
  },

  email() {
    return localStorage.getItem(this.userKey);
  },

  role() {
    return localStorage.getItem(this.roleKey);
  },

  userId() {
    return localStorage.getItem(this.userIdKey);
  },

  isLoggedIn() {
    return Boolean(this.getToken());
  },

  isAdmin() {
    return this.role() === "ADMIN";
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
  if (status === 403) {
    return (data && (data.message || data.error)) || "Forbidden — admin role required.";
  }
  if (status === 401) {
    return (data && (data.message || data.error)) || "Unauthorized — please log in.";
  }
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
