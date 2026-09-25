const BASE_URL_TODOS = "/api/todos";

export async function fetchTodos() {
    try {
        const response = await fetch(BASE_URL_TODOS);
        if (!response.ok) {
            throw new Error(`Failed to fetch todos: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error(error);
        return [];
    }
}

export async function fetchTodoById(id) {
    if (!id) {
        return;
    }
    const response = await fetch(`${BASE_URL_TODOS}/${id}`);
    if (!response.ok) {
        throw new Error(`Failed to fetch todos: ${response.status}`);
    }
    return await response.json();
}

export async function addTodo(todo) {
    try {
        const response = await fetch(BASE_URL_TODOS, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(todo)
        });
        if (!response.ok) {
            throw new Error(`Failed to add todo: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error(error);
    }
}

export async function updateTodo(id, updatedTodo) {
    try {
        const response = await fetch(`${BASE_URL_TODOS}/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedTodo)
        });
        if (!response.ok) {
            throw new Error(`Failed to update todo: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error(error);
    }
}

export async function deleteTodo(id) {
    try {
        const response = await fetch(`${BASE_URL_TODOS}/${id}`, {
            method: "DELETE"
        });
        if (!response.ok) {
            throw new Error(`Failed to delete todo: ${response.status}`);
        }
        return true;
    } catch (error) {
        console.error(error);
        return false;
    }
}