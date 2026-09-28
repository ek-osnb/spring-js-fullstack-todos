const BASE_URL_TODOS = "/api/todos";

async function ensureOk(response, fallbackMessage) {
    if (response.ok) {
        return;
    }
    let message = `${fallbackMessage}: ${response.status}`;
    try {
        const problem = await response.json();
        if (problem.detail) {
            message = problem.detail;
        }
    } catch {
        // response body was not JSON - keep the fallback message
    }
    throw new Error(message);
}

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
