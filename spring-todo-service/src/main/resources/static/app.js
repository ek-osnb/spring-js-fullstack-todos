import { fetchTodos, addTodo, updateTodo, deleteTodo, completeTodo, reopenTodo } from "./api/todo.api.js"
import { fetchUsers } from "./api/user.api.js"
import { sortBy } from "./utils/sorting.js"

document.addEventListener("DOMContentLoaded", initApp);

let allTodos = [];
let usersById = new Map();

const sortState = {
    key: "title",
    isAsc: true
};

async function initApp() {
    document.querySelector("#todoForm").addEventListener("submit", handleFormSubmit);
    document.querySelector("#todoTableBody").addEventListener("click", handleTableClick);
    document.querySelector("#todoTableHeader").addEventListener("click", handleHeaderClick);
    document.querySelector("#searchBox").addEventListener("input", render);
    document.querySelector("#cancelEdit").addEventListener("click", resetForm);
    await loadUsers();
    await refreshTodos();
}

async function loadUsers() {
    try {
        const users = await fetchUsers();
        usersById = new Map(users.map(u => [u.id, u]));
    } catch (error) {
        showError(error.message);
        return;
    }

    const select = document.querySelector("#userId");
    for (const user of usersById.values()) {
        const option = document.createElement("option");
        option.value = user.id;
        option.textContent = user.username;
        select.appendChild(option);
    }
}

async function refreshTodos() {
    try {
        const todos = await fetchTodos();
        // Add the username so the table can display and sort by it
        allTodos = todos.map(t => ({
            ...t,
            username: usersById.get(t.userId)?.username ?? String(t.userId)
        }));
    } catch (error) {
        showError(error.message);
        allTodos = [];
    }
    render();
}

// filter -> sort -> display
function render() {
    const searchTerm = document.querySelector("#searchBox").value.trim().toLowerCase();
    const visibleTodos = allTodos
        .filter(t => t.title.toLowerCase().includes(searchTerm))
        .sort(sortBy(sortState.key, sortState.isAsc));
    displayTodos(visibleTodos);
    updateSortIndicator();
}

function showError(message) {
    const errorBox = document.querySelector("#errorBox");
    errorBox.textContent = message;
    errorBox.classList.remove("d-none");
}

function clearError() {
    const errorBox = document.querySelector("#errorBox");
    errorBox.textContent = "";
    errorBox.classList.add("d-none");
}

function handleHeaderClick(e) {
    const col = e.target.closest("th");
    const key = col?.getAttribute("data-sort-key");
    if (!key) {
        return;
    }

    if (sortState.key === key) {
        sortState.isAsc = !sortState.isAsc;
    }

    if (sortState.key !== key) {
        sortState.key = key;
        sortState.isAsc = true;
    }
    render();
}

function updateSortIndicator() {
    const asc = "▲";
    const desc = "▼";

    // RESET
    document.querySelectorAll(".sort-indicator").forEach(s => {
        s.textContent = "";
    });

    // sort-title eller sort-username
    const span = document.querySelector(`#sort-${sortState.key}`);
    span.textContent = sortState.isAsc ? asc : desc;
}

function displayTodos(todos) {
    const tableBody = document.querySelector("#todoTableBody");
    tableBody.innerHTML = ""; // Clear existing rows
    for (const todo of todos) {
        renderTodoRow(todo);
    }
}

function renderTodoRow(todo) {
    const tableBody = document.querySelector("#todoTableBody");

    const row = document.createElement("tr");
    row.setAttribute("data-id", todo.id);

    const titleCell = document.createElement("td");
    titleCell.textContent = todo.title;

    const userCell = document.createElement("td");
    userCell.textContent = todo.username;

    const completedCell = document.createElement("td");
    const completedCheckbox = document.createElement("input");
    completedCheckbox.type = "checkbox";
    completedCheckbox.className = "form-check-input";
    completedCheckbox.setAttribute("data-action", "toggle");
    completedCheckbox.checked = todo.completed;
    completedCell.appendChild(completedCheckbox);

    const actionsCell = document.createElement("td");

    const editButton = document.createElement("button");
    editButton.className = "btn btn-warning";
    editButton.setAttribute("data-action", "edit");
    editButton.textContent = "Edit";

    const deleteButton = document.createElement("button");
    deleteButton.className = "btn btn-danger";
    deleteButton.setAttribute("data-action", "delete");
    deleteButton.textContent = "Delete";

    actionsCell.append(editButton, deleteButton);
    row.append(titleCell, userCell, completedCell, actionsCell);
    tableBody.appendChild(row);
}

async function handleFormSubmit(event) {
    event.preventDefault();
    const form = new FormData(event.target);
    const id = form.get("id");
    const title = form.get("title");
    const userId = Number(form.get("userId"));
    const completed = form.get("completed") === "on";

    const todoData = { title, userId, completed };

    try {
        if (id) {
            await updateTodo(id, todoData);
        } else {
            await addTodo(todoData);
        }
    } catch (error) {
        showError(error.message);
        return;
    }

    clearError();
    resetForm();
    await refreshTodos();
}

function resetForm() {
    document.querySelector("#todoForm").reset();
    // reset() doesn't clear hidden inputs
    document.querySelector("#todoId").value = "";
    setEditMode(false);
}

function setEditMode(isEditing) {
    document.querySelector("#submitButton").textContent = isEditing ? "Update Todo" : "Add Todo";
    document.querySelector("#cancelEdit").classList.toggle("d-none", !isEditing);
}

async function handleTableClick(event) {
    const action = event.target.getAttribute("data-action");
    const row = event.target.closest("tr");
    if (!row) {
        return;
    }
    const id = row.getAttribute("data-id");

    if (action === null) {
        window.location.href = `todos.html?id=${id}`;
        return;
    }

    if (action === "toggle") {
        try {
            if (event.target.checked) {
                await completeTodo(id);
            } else {
                await reopenTodo(id);
            }
            clearError();
        } catch (error) {
            showError(error.message);
        }
        // Refresh either way so the checkbox always matches the server
        await refreshTodos();
    } else if (action === "delete") {
        if (!confirm("Delete this todo?")) {
            return;
        }
        try {
            await deleteTodo(id);
            clearError();
        } catch (error) {
            showError(error.message);
        }
        await refreshTodos();
    } else if (action === "edit") {
        const todo = allTodos.find(t => String(t.id) === id);
        if (!todo) {
            return;
        }

        document.querySelector("#todoId").value = todo.id;
        document.querySelector("#todoTitle").value = todo.title;
        document.querySelector("#userId").value = todo.userId;
        document.querySelector("#completed").checked = todo.completed;
        setEditMode(true);
    }
}