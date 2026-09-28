import {fetchTodoById} from "./api/todo.api.js";

const container = document.querySelector("#todo-details");
const urlParams = new URLSearchParams(window.location.search);
const todoId = urlParams.get("id");
displayTodoById(todoId);

async function displayTodoById(id) {
    if (!id) {
        displayNotFound();
        return;
    }
    try {
        const data = await fetchTodoById(id);
        container.replaceChildren(
            detailRow("ID", data.id),
            detailRow("Title", data.title),
            detailRow("User ID", data.userId),
            detailRow("Completed", data.completed ? "Yes" : "No")
        );
    } catch (err) {
        displayNotFound();
    }
}

function detailRow(label, value) {
    const p = document.createElement("p");
    const strong = document.createElement("strong");
    strong.textContent = `${label}:`;
    p.append(strong, ` ${value}`);
    return p;
}

function displayNotFound() {
    container.innerHTML = `<h2>404 - not found</h2>`;
}