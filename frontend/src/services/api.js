const API_BASE = import.meta.env.VITE_API_BASE || "/api";

function buildQuery(params = {}) {
  const entries = Object.entries(params || {}).filter(([, value]) => value !== undefined && value !== null);
  if (entries.length === 0) {
    return "";
  }
  const search = new URLSearchParams();
  entries.forEach(([key, value]) => search.append(key, value));
  return `?${search.toString()}`;
}

async function extractErrorMessage(response) {
  const text = await response.text();
  if (!text) return `Request failed: ${response.status}`;
  try {
    const json = JSON.parse(text);
    return json.message || json.error || text;
  } catch {
    return text;
  }
}

async function request(path, options = {}) {
  const token = localStorage.getItem("token");
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      "Accept-Language": "ar",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    if (window.location.pathname !== "/login") {
      window.location.href = "/login";
    }
    throw new Error("Unauthorized");
  }

  if (response.status === 204) {
    return null;
  }

  if (!response.ok) {
    throw new Error(await extractErrorMessage(response));
  }

  return response.json();
}

export function apiGet(path, params) {
  const query = buildQuery(params);
  return request(`${path}${query}`);
}

export function apiPost(path, body) {
  return request(path, {
    method: "POST",
    body: JSON.stringify(body)
  });
}

export function apiPut(path, body) {
  return request(path, {
    method: "PUT",
    body: JSON.stringify(body)
  });
}

export function apiDelete(path) {
  return request(path, {
    method: "DELETE"
  });
}

export function apiPatch(path, body) {
  return request(path, {
    method: "PATCH",
    body: JSON.stringify(body)
  });
}
