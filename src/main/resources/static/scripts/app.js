const loginForm = document.querySelector("#login-form");
const registerForm = document.querySelector("#register-form");

document.querySelectorAll("[data-show]").forEach((button) => {
    button.addEventListener("click", () => {
        showForm(button.dataset.show);
    });
});

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = Object.fromEntries(new FormData(loginForm));

    await submitJson({
        form: loginForm,
        messageId: "login-message",
        url: "/auth/login",
        body: {
            correo: data.correo,
            contrasena: data.contrasena
        },
        successMessage: "Inicio de sesion exitoso."
    });
});

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = Object.fromEntries(new FormData(registerForm));
    const message = document.querySelector("#register-message");

    if (data.contrasena !== data.confirmar) {
        setMessage(message, "Las contrasenas no coinciden.", "error");
        return;
    }

    const created = await submitJson({
        form: registerForm,
        messageId: "register-message",
        url: "/usuarios",
        body: {
            nombre: data.nombre,
            correo: data.correo,
            contrasena: data.contrasena
        },
        successMessage: "Cuenta creada correctamente. Ahora inicia sesion."
    });

    if (created) {
        loginForm.elements.correo.value = data.correo;
        registerForm.reset();
        setTimeout(() => showForm("login-form"), 700);
    }
});

function showForm(formId) {
    loginForm.classList.toggle("hidden", formId !== "login-form");
    registerForm.classList.toggle("hidden", formId !== "register-form");
    clearMessages();
}

async function submitJson({ form, messageId, url, body, successMessage }) {
    const message = document.querySelector(`#${messageId}`);
    const button = form.querySelector("button[type='submit']");

    setMessage(message, "", "");
    button.disabled = true;

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "same-origin",
            body: JSON.stringify(body)
        });

        const responseBody = await readJson(response);

        if (!response.ok) {
            setMessage(message, errorMessage(responseBody), "error");
            return false;
        }

        setMessage(message, successMessage, "success");
        return true;
    } catch (error) {
        setMessage(message, "No se pudo conectar con la API.", "error");
        return false;
    } finally {
        button.disabled = false;
    }
}

async function readJson(response) {
    const text = await response.text();

    if (!text) {
        return {};
    }

    try {
        return JSON.parse(text);
    } catch (error) {
        return { mensaje: text };
    }
}

function errorMessage(body) {
    if (body.mensaje) {
        return body.mensaje;
    }

    if (body.mensajes) {
        return Object.values(body.mensajes).join(" ");
    }

    return "Ocurrio un error.";
}

function setMessage(element, text, type) {
    element.textContent = text;
    element.classList.remove("success", "error");

    if (type) {
        element.classList.add(type);
    }
}

function clearMessages() {
    document.querySelectorAll(".message").forEach((message) => {
        setMessage(message, "", "");
    });
}
