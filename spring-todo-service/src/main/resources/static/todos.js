import {fetchTodoById} from "./api/todo.api.js";

const urlParams = new URLSearchParams(window.location.search);
const todoId = urlParams.get("id");
displayTodoById(todoId);
const container = document.querySelector("#todo-details");

async function displayTodoById(id) {
    try {
        const data = await fetchTodoById(todoId)

        container.innerHTML = `
            <p><strong>ID:</strong> ${data.id}</p>
            <p><strong>Title:</strong> ${data.title}</p>
            <p><strong>User ID:</strong> ${data.userId}</p>
            <p><strong>Completed:</strong> ${data.completed ? "Yes" : "No"}</p>
        `;

    } catch (err) {
        displayNotFound();
    }

}


function displayNotFound() {
    container.innerHTML = `<h2>404 - not found</h2>`;
}