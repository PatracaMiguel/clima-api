const profileForm = document.querySelector("#profile-form");
const profileMessage = document.querySelector("#profile-message");
const profileAvatar = document.querySelector("#profile-avatar");
const profileNameTitle = document.querySelector("#profile-name-title");
const profileEmailTitle = document.querySelector("#profile-email-title");
const profileNombre = document.querySelector("#profile-nombre");
const profileCorreo = document.querySelector("#profile-correo");
const profileContrasena = document.querySelector("#profile-contrasena");

const storedUser = getStoredUser();

if (!storedUser?.idUsuario) {
    window.location.href = "/index.html";
}

let currentUser = {};

profileForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const changes = getChanges();

    if (changes.length === 0) {
        setMessage(profileMessage, "No hay cambios para guardar.", "error");
        return;
    }

    if (changes.length > 1) {
        setMessage(profileMessage, "Cambia solo un dato a la vez.", "error");
        return;
    }

    const [change] = changes;
    const button = profileForm.querySelector("button[type='submit']");
    button.disabled = true;
    setMessage(profileMessage, "", "");

    const response = await requestJson(`/usuarios/${encodeURIComponent(storedUser.idUsuario)}`, {
        method: "PATCH",
        body: { [change.field]: change.value }
    });

    button.disabled = false;

    if (!response.ok) {
        setMessage(profileMessage, response.message, "error");
        return;
    }

    currentUser = response.data;
    localStorage.setItem("climaUsuario", JSON.stringify({
        idUsuario: currentUser.id,
        nombre: currentUser.nombre,
        correo: currentUser.correo
    }));

    renderUser(currentUser);
    fillForm(currentUser);
    setMessage(profileMessage, "Cambio guardado correctamente.", "success");
});

loadUser();

async function loadUser() {
    setMessage(profileMessage, "Cargando perfil...", "");

    const response = await requestJson(`/usuarios/${encodeURIComponent(storedUser.idUsuario)}`);

    if (!response.ok) {
        setMessage(profileMessage, response.message, "error");
        return;
    }

    currentUser = response.data;
    renderUser(currentUser);
    fillForm(currentUser);
    setMessage(profileMessage, "", "");
}

function getChanges() {
    const changes = [];
    const nombre = profileNombre.value.trim();
    const correo = profileCorreo.value.trim();
    const contrasena = profileContrasena.value.trim();

    if (nombre !== (currentUser.nombre || "")) {
        if (!nombre) {
            return [{ field: "nombre", value: "" }];
        }

        changes.push({ field: "nombre", value: nombre });
    }

    if (correo !== (currentUser.correo || "")) {
        if (!correo) {
            return [{ field: "correo", value: "" }];
        }

        changes.push({ field: "correo", value: correo });
    }

    if (contrasena) {
        changes.push({ field: "contrasena", value: contrasena });
    }

    return changes;
}

function fillForm(user) {
    profileNombre.value = user.nombre || "";
    profileCorreo.value = user.correo || "";
    profileContrasena.value = "";
}

function renderUser(user) {
    const name = user.nombre || "Tu perfil";
    const email = user.correo || "tu@correo.com";

    profileAvatar.textContent = name.trim().charAt(0).toUpperCase() || "?";
    profileNameTitle.textContent = name;
    profileEmailTitle.textContent = email;
}

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

function getStoredUser() {
    try {
        return JSON.parse(localStorage.getItem("climaUsuario"));
    } catch (error) {
        return null;
    }
}
