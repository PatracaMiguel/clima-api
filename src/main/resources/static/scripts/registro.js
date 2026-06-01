const registerForm = document.querySelector("#register-form");
const registerMessage = document.querySelector("#register-message");

document.querySelectorAll("[data-toggle-password]").forEach((button) => {
    button.addEventListener("click", () => {
        const input = document.querySelector(`#${button.dataset.togglePassword}`);
        const isHidden = input.type === "password";

        input.type = isHidden ? "text" : "password";
        button.textContent = isHidden ? "Ocultar" : "Mostrar";
    });
});

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = Object.fromEntries(new FormData(registerForm));
    const button = registerForm.querySelector("button[type='submit']");

    if (data.contrasena !== data.confirmar) {
        setMessage(registerMessage, "Las contrasenas no coinciden.", "error");
        return;
    }

    setMessage(registerMessage, "", "");
    button.disabled = true;

    const response = await requestJson("/usuarios", {
        method: "POST",
        body: {
            nombre: data.nombre,
            correo: data.correo,
            contrasena: data.contrasena
        }
    });

    button.disabled = false;

    if (!response.ok) {
        setMessage(registerMessage, response.message, "error");
        return;
    }

    setMessage(registerMessage, "Cuenta creada correctamente. Ahora inicia sesion.", "success");
    registerForm.reset();
    setTimeout(() => {
        window.location.href = "/index.html";
    }, 900);
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
