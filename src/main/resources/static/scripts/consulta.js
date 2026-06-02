const weatherForm = document.querySelector("#weather-form");
const dashboardMessage = document.querySelector("#dashboard-message");
const searchView = document.querySelector("#search-view");
const resultView = document.querySelector("#result-view");
let currentFavorite = null;

if (!getStoredUser()) {
    window.location.href = "/index.html";
}

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
    window.location.href = "/index.html";
});

document.querySelector("#back-button").addEventListener("click", () => {
    showSearchView();
});

document.querySelector("#profile-button").addEventListener("click", () => {
    window.location.href = "/perfil.html";
});

document.querySelector("#favorite-button").addEventListener("click", async () => {
    await saveCurrentFavorite();
});

loadPendingCity();

async function loadPendingCity() {
    const city = localStorage.getItem("climaCiudadPendiente");
    if (!city) {
        return;
    }

    localStorage.removeItem("climaCiudadPendiente");
    weatherForm.elements.ciudad.value = city;
    await loadCity(city);
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
                window.location.href = "/index.html";
            }

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
