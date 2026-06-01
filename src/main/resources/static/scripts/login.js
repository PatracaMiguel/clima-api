const loginForm = document.querySelector("#login-form");
const loginMessage = document.querySelector("#login-message");

if (getStoredUser()) {
    window.location.href = "/consulta.html";
}

document.querySelectorAll("[data-toggle-password]").forEach((button) => {
    button.addEventListener("click", () => {
        const input = document.querySelector(`#${button.dataset.togglePassword}`);
        const isHidden = input.type === "password";

        input.type = isHidden ? "text" : "password";
        button.textContent = isHidden ? "Ocultar" : "Mostrar";
    });
});

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = Object.fromEntries(new FormData(loginForm));
    const button = loginForm.querySelector("button[type='submit']");

    setMessage(loginMessage, "", "");
    button.disabled = true;

    const response = await requestJson("/auth/login", {
        method: "POST",
        body: {
            correo: data.correo,
            contrasena: data.contrasena
        }
    });

    button.disabled = false;

    if (!response.ok) {
        setMessage(loginMessage, response.message, "error");
        return;
    }

    localStorage.setItem("climaUsuario", JSON.stringify(response.data));
    loginForm.reset();
    window.location.href = "/consulta.html";
});

async function requestJson(url, options = {}) {
    try {
        const response = await fetch(url, {
            method: options.method || "GET",
            headers: options.body ? { "Content-Type": "application/json" } : {},
            credentials: "same-origin",
            body: options.body ? JSON.stringify(options.body) : undefined
        });

        const data = await readJson(response);

        if (!response.ok) {
            return { ok: false, data, message: errorMessage(data) };
        }

        return { ok: true, data, message: "" };
    } catch (error) {
        return { ok: false, data: {}, message: "No se pudo conectar con la API." };
    }
}

async function readJson(response) {
    const text = await response.text();
    if (!text) return {};

    try {
        return JSON.parse(text);
    } catch (error) {
        return { mensaje: text };
    }
}

function errorMessage(body) {
    if (body.mensaje) return body.mensaje;
    if (body.mensajes) return Object.values(body.mensajes).join(" ");
    return "Ocurrio un error.";
}

function setMessage(element, text, type) {
    element.textContent = text;
    element.classList.remove("success", "error");
    if (type) element.classList.add(type);
}

function getStoredUser() {
    try {
        return JSON.parse(localStorage.getItem("climaUsuario"));
    } catch (error) {
        return null;
    }
}
