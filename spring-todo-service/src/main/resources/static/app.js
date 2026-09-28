import { fetchTodos, addTodo, updateTodo, deleteTodo } from "./api/todo.api.js"
import { sortBy } from "./utils/sorting.js"

document.addEventListener("DOMContentLoaded", initApp);

let allTodos = [];

const sortState = {
    key: "title",
    isAsc: true
};

async function initApp() {
    document.querySelector("#todoForm").addEventListener("submit", handleFormSubmit);
    document.querySelector("#todoTableBody").addEventListener("click", handleTableClick);
    document.querySelector("#todoTableHeader").addEventListener("click", handleHeaderClick);
    document.querySelector("#searchBox").addEventListener("input", render);
    await refreshTodos();
}

async function refreshTodos() {
    try {
        allTodos = await fetchTodos();
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

    // sort-title eller sort-userid
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

    const userIdCell = document.createElement("td");
    userIdCell.textContent = todo.userId;

    const completedCell = document.createElement("td");
    completedCell.textContent = todo.completed ? "Yes" : "No";

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
    row.append(titleCell, userIdCell, completedCell, actionsCell);
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
    event.target.reset();
    document.querySelector("#todoId").value = "";

    await refreshTodos();
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

    if (action === "delete") {
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
    }
}