const authPage = document.querySelector("#auth-page");
const dashboard = document.querySelector("#dashboard");
const loginForm = document.querySelector("#login-form");
const registerForm = document.querySelector("#register-form");
const weatherForm = document.querySelector("#weather-form");
const dashboardMessage = document.querySelector("#dashboard-message");
const searchView = document.querySelector("#search-view");
const resultView = document.querySelector("#result-view");
let currentFavorite = null;

document.querySelectorAll("[data-show]").forEach((button) => {
    button.addEventListener("click", () => {
        showForm(button.dataset.show);
    });
});

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
    const response = await submitJson({
        form: loginForm,
        messageId: "login-message",
        url: "/auth/login",
        body: {
            correo: data.correo,
            contrasena: data.contrasena
        },
        successMessage: "Inicio de sesion exitoso."
    });

    if (response) {
        localStorage.setItem("climaUsuario", JSON.stringify(response));
        loginForm.reset();
        openDashboard();
    }
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

weatherForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const data = Object.fromEntries(new FormData(weatherForm));
    const city = data.ciudad.trim();

    if (!city) {
        setMessage(dashboardMessage, "Escribe una ciudad para consultar.", "error");
        return;
    }

    await loadCity(city);
});

document.querySelector("#logout-button").addEventListener("click", async () => {
    await fetch("/auth/logout", {
        method: "POST",
        credentials: "same-origin"
    });

    localStorage.removeItem("climaUsuario");
    dashboard.classList.add("hidden");
    authPage.classList.remove("hidden");
    clearDashboard();
});

document.querySelector("#back-button").addEventListener("click", () => {
    showSearchView();
});

document.querySelector("#profile-button").addEventListener("click", () => {
    window.location.href = "/perfil.html";
});

ensureFavoriteButton();

const storedUser = getStoredUser();

if (storedUser) {
    openDashboard();
    loadPendingCity();
}

function openDashboard() {
    authPage.classList.add("hidden");
    dashboard.classList.remove("hidden");
    showSearchView();
}

function ensureFavoriteButton() {
    const resultPanel = document.querySelector(".result-panel");
    const metrics = document.querySelector(".metrics");
    let favoriteButton = document.querySelector("#favorite-button");

    if (!favoriteButton && resultPanel && metrics) {
        const action = document.createElement("div");
        action.id = "favorite-action";
        action.className = "favorite-action";

        favoriteButton = document.createElement("button");
        favoriteButton.id = "favorite-button";
        favoriteButton.className = "favorite-button";
        favoriteButton.type = "button";
        favoriteButton.textContent = "Guardar en favoritos";

        action.appendChild(favoriteButton);
        metrics.insertAdjacentElement("afterend", action);
    }

    favoriteButton?.addEventListener("click", async () => {
        await saveCurrentFavorite();
    });
}

async function loadPendingCity() {
    const city = localStorage.getItem("climaCiudadPendiente");
    if (!city) {
        return;
    }

    localStorage.removeItem("climaCiudadPendiente");
    weatherForm.elements.ciudad.value = city;
    await loadCity(city);
}

function showForm(formId) {
    loginForm.classList.toggle("hidden", formId !== "login-form");
    registerForm.classList.toggle("hidden", formId !== "register-form");
    clearMessages();
}

async function loadCity(city) {
    setMessage(dashboardMessage, "Consultando clima...", "");
    weatherForm.querySelector("button").disabled = true;

    const [weather, recommendation, forecast] = await Promise.all([
        requestJson(`/clima/${encodeURIComponent(city)}`),
        requestJson(`/recomendaciones/${encodeURIComponent(city)}`),
        requestJson(`/clima/${encodeURIComponent(city)}/pronostico`)
    ]);

    weatherForm.querySelector("button").disabled = false;

    if (!weather.ok) {
        setMessage(dashboardMessage, weather.message, "error");
        return;
    }

    renderWeather(city, weather.data);
    renderRecommendation(recommendation.ok ? recommendation.data : null);
    renderForecast(forecast.ok ? forecast.data.pronostico : []);
    setMessage(dashboardMessage, "Consulta actualizada.", "success");
    showResultView();
}

function renderWeather(city, weather) {
    const main = weather.main || {};
    const firstWeather = Array.isArray(weather.weather) ? weather.weather[0] : {};
    const country = weather.sys?.country || "Desconocido";
    const resolvedCity = (weather.name || city || "").trim();

    currentFavorite = {
        ciudad: resolvedCity,
        pais: country
    };

    document.querySelector("#current-city").textContent = resolvedCity || city;
    document.querySelector("#current-temp").textContent = formatTemp(main.temp);
    document.querySelector("#feels-like").textContent = formatTemp(main.feels_like);
    document.querySelector("#weather-main").textContent = firstWeather.main || "--";
    document.querySelector("#current-description").textContent = firstWeather.description || "Sin descripcion";
}

async function saveCurrentFavorite() {
    const favoriteButton = document.querySelector("#favorite-button");
    const displayedCity = document.querySelector("#current-city").textContent.trim();
    const searchedCity = weatherForm.elements.ciudad.value.trim();
    const cityToSave = currentFavorite?.ciudad || displayedCity || searchedCity;

    if (!cityToSave) {
        setMessage(dashboardMessage, "Busca una ciudad antes de guardarla.", "error");
        return;
    }

    currentFavorite = {
        ciudad: cityToSave,
        pais: currentFavorite?.pais || "Desconocido"
    };

    favoriteButton.disabled = true;

    const response = await requestJson("/favoritos", {
        method: "POST",
        body: currentFavorite
    });

    favoriteButton.disabled = false;

    if (!response.ok) {
        setMessage(dashboardMessage, response.message || "No se pudo guardar el favorito.", "error");
        return;
    }

    window.location.href = "/favoritos.html";
}

function renderRecommendation(recommendation) {
    document.querySelector("#clothing-recommendation").textContent =
        recommendation?.recomendacionRopa || "No se pudo cargar la recomendacion de ropa.";
    document.querySelector("#accessory-recommendation").textContent =
        recommendation?.recomendacionAccesorios || "";
    document.querySelector("#weather-advice").textContent =
        recommendation?.mensaje || "";
}

function renderForecast(slots) {
    const forecastList = document.querySelector("#forecast-list");
    const nextSlots = Array.isArray(slots) ? slots.slice(0, 4) : [];

    if (!nextSlots.length) {
        forecastList.className = "forecast-list empty-state";
        forecastList.textContent = "Sin pronostico disponible.";
        return;
    }

    forecastList.className = "forecast-list";
    forecastList.innerHTML = nextSlots.map((slot) => `
        <div class="forecast-item">
            <strong>${formatTemp(slot.temp)}</strong>
            <span>${formatHour(slot.hora)}</span>
            <span>${escapeHtml(slot.descripcion || "Sin descripcion")}</span>
        </div>
    `).join("");
}

async function submitJson({ form, messageId, url, body, successMessage }) {
    const message = document.querySelector(`#${messageId}`);
    const button = form.querySelector("button[type='submit']");

    setMessage(message, "", "");
    button.disabled = true;

    const response = await requestJson(url, { method: "POST", body });

    button.disabled = false;

    if (!response.ok) {
        setMessage(message, response.message, "error");
        return null;
    }

    setMessage(message, successMessage, "success");
    return response.data;
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
            if (data.mensaje?.includes("iniciar sesion")) {
                localStorage.removeItem("climaUsuario");
                dashboard.classList.add("hidden");
                authPage.classList.remove("hidden");
            }

            return {
                ok: false,
                data,
                message: errorMessage(data)
            };
        }

        return { ok: true, data, message: "" };
    } catch (error) {
        return {
            ok: false,
            data: {},
            message: "No se pudo conectar con la API."
        };
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

function clearDashboard() {
    weatherForm.reset();
    document.querySelector("#current-city").textContent = "Busca una ciudad";
    document.querySelector("#current-temp").textContent = "--";
    document.querySelector("#current-description").textContent = "Sin datos cargados.";
    document.querySelector("#feels-like").textContent = "--";
    document.querySelector("#weather-main").textContent = "--";
    document.querySelector("#clothing-recommendation").textContent = "Consulta una ciudad para recibir una recomendacion de ropa.";
    document.querySelector("#accessory-recommendation").textContent = "";
    document.querySelector("#weather-advice").textContent = "";
    document.querySelector("#forecast-list").className = "forecast-list empty-state";
    document.querySelector("#forecast-list").textContent = "Busca una ciudad para ver el pronostico.";
    currentFavorite = null;
    showSearchView();
    clearMessages();
}

function showSearchView() {
    searchView.classList.remove("hidden");
    resultView.classList.add("hidden");
}

function showResultView() {
    searchView.classList.add("hidden");
    resultView.classList.remove("hidden");
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function getStoredUser() {
    try {
        return JSON.parse(localStorage.getItem("climaUsuario"));
    } catch (error) {
        return null;
    }
}

function formatTemp(value) {
    if (value === null || value === undefined || Number.isNaN(Number(value))) {
        return "--";
    }

    return `${Math.round(Number(value))} C`;
}

function formatHour(value) {
    if (!value) {
        return "Hora no disponible";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return new Intl.DateTimeFormat("es-MX", {
        weekday: "short",
        hour: "2-digit",
        minute: "2-digit"
    }).format(date);
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, (character) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "\"": "&quot;",
        "'": "&#039;"
    })[character]);
}
