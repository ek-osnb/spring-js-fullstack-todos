import { ensureOk } from "./http.js";

const BASE_URL_TODOS = "/api/todos";

export async function fetchTodos() {
    const response = await fetch(BASE_URL_TODOS);
    await ensureOk(response, "Failed to fetch todos");
    return await response.json();
}

export async function fetchTodoById(id) {
    const response = await fetch(`${BASE_URL_TODOS}/${id}`);
    await ensureOk(response, "Failed to fetch todo");
    return await response.json();
}

export async function addTodo(todo) {
    const response = await fetch(BASE_URL_TODOS, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(todo)
    });
    await ensureOk(response, "Failed to add todo");
    return await response.json();
}

export async function updateTodo(id, updatedTodo) {
    const response = await fetch(`${BASE_URL_TODOS}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedTodo)
    });
    await ensureOk(response, "Failed to update todo");
    return await response.json();
}

export async function deleteTodo(id) {
    const response = await fetch(`${BASE_URL_TODOS}/${id}`, {
        method: "DELETE"
    });
    await ensureOk(response, "Failed to delete todo");
}

export async function completeTodo(id) {
    const response = await fetch(`${BASE_URL_TODOS}/${id}/complete`, {
        method: "PATCH"
    });
    await ensureOk(response, "Failed to complete todo");
    return await response.json();
}

export async function reopenTodo(id) {
    const response = await fetch(`${BASE_URL_TODOS}/${id}/reopen`, {
        method: "PATCH"
    });
    await ensureOk(response, "Failed to reopen todo");
    return await response.json();
}
