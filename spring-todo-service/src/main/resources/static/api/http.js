// Throws an Error with the server's ProblemDetail "detail" when available
export async function ensureOk(response, fallbackMessage) {
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
