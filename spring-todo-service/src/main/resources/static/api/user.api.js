import { ensureOk } from "./http.js";

const BASE_URL_USERS = "/api/users";

export async function fetchUsers() {
    const response = await fetch(BASE_URL_USERS);
    await ensureOk(response, "Failed to fetch users");
    return await response.json();
}
