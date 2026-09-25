document.addEventListener("DOMContentLoaded", initApp);

// const BASE_URL_TODOS = "https://jsonplaceholder.typicode.com/todos";
const BASE_URL_TODOS = "http://localhost:8080/api/todos";

let todosData = [];
let refData = [];

const sortState = {
    key: "title",
    isAsc: true
};

async function initApp() {
    await refreshTodos();
    document.querySelector("#todoForm").addEventListener("submit", handleFormSubmit);
    document.querySelector("#todoTableBody").addEventListener("click", handleTableClick);
    document.querySelector("#todoTableHeader").addEventListener("click", handleHeaderClick);
    document.querySelector("#searchBox").addEventListener("input", handleSearchInput);
}

(e) => handleSearchInput(e, param2)

function handleSearchInput(e) {
    const searchTerm = e.target.value;
    console.log("BEFORE:", todosData.length);
    todosData = refData.filter(t => t.title.includes(searchTerm));
    sortAndDisplay();
}

async function refreshTodos() {
    todosData = await fetchTodos();
    refData = todosData;
    displayTodos(todosData);
}

function sortAndDisplay() {
    todosData.sort(sortBy(sortState.key, sortState.isAsc));
    displayTodos(todosData);
}

function handleHeaderClick(e) {
    const col = e.target.closest("th");
    const key = col.getAttribute("data-sort-key");
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
    sortAndDisplay();
    updateSortIndicator();
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

function sortBy(key, isAsc = true) {
    return (a, b) => {

        const aVal = a[key];
        const bVal = b[key];

        if (typeof aVal === "string" && typeof bVal === "string") {
            const result = aVal.localeCompare(bVal);
            // if (isAsc) {
            //     return result;
            // } else {
            //     return -result;
            // }
            return isAsc ? result : -result;
        }

        if (typeof aVal === "number" && typeof bVal === "number") {
            const result = aVal - bVal;
            return isAsc ? result : -result;
        }

        return 0;
    }
}

async function fetchTodos() {
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

async function addTodo(todo) {
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

async function updateTodo(id, updatedTodo) {
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

async function deleteTodo(id) {
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

async function handleFormSubmit(event) {
    event.preventDefault();
    const form = new FormData(event.target);
    const id = form.get("id");
    const title = form.get("title");
    const userId = Number(form.get("userId"));
    const completed = form.get("completed") === "on";

    const todoData = { title, userId, completed };

    if (id) {
        await updateTodo(id, todoData);
    } else {
        await addTodo(todoData);
    }

    event.target.reset();
    document.querySelector("#todoId").value = "";

    await refreshTodos();
}

async function handleTableClick(event) {
    const action = event.target.getAttribute("data-action");
    const row = event.target.closest("tr");
    const id = row.getAttribute("data-id");

    if (action === "delete") {
        await deleteTodo(id);
        await refreshTodos();
    } else if (action === "edit") {
        const title = row.children[0].textContent;
        const userId = row.children[1].textContent;
        const completed = row.children[2].textContent === "Yes";

        document.querySelector("#todoId").value = id;
        document.querySelector("#todoTitle").value = title;
        document.querySelector("#userId").value = userId;
        document.querySelector("#completed").checked = completed;
    }
}