(function () {
  const STORAGE_KEY = "miniacademy-theme";
  const DARK = "dark";
  const LIGHT = "light";

  function getSystemTheme() {
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: light)").matches ? LIGHT : DARK;
  }

  function getSavedTheme() {
    try {
      const savedTheme = localStorage.getItem(STORAGE_KEY);
      return savedTheme === LIGHT || savedTheme === DARK ? savedTheme : null;
    } catch (error) {
      return null;
    }
  }

  function applyTheme(theme) {
    document.documentElement.setAttribute("data-theme", theme);

    const isLight = theme === LIGHT;
    const toggleButtons = document.querySelectorAll("[data-theme-toggle]");

    toggleButtons.forEach((button) => {
      const icon = button.querySelector(".theme-toggle-icon");
      const text = button.querySelector(".theme-toggle-text");

      button.setAttribute("aria-pressed", String(isLight));
      button.setAttribute("aria-label", isLight ? "Activar modo oscuro" : "Activar modo claro");
      button.setAttribute("title", isLight ? "Cambiar a modo oscuro" : "Cambiar a modo claro");

      if (icon) {
        icon.textContent = isLight ? "☀" : "☾";
      }

      if (text) {
        text.textContent = isLight ? "Modo oscuro" : "Modo claro";
      }
    });
  }

  function setTheme(theme) {
    applyTheme(theme);

    try {
      localStorage.setItem(STORAGE_KEY, theme);
    } catch (error) {
      // Ignore storage errors in restrictive browser environments.
    }
  }

  const initialTheme = getSavedTheme() || getSystemTheme();
  applyTheme(initialTheme);

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
      button.addEventListener("click", function () {
        const currentTheme = document.documentElement.getAttribute("data-theme") || DARK;
        const nextTheme = currentTheme === LIGHT ? DARK : LIGHT;
        setTheme(nextTheme);
      });
    });

    applyTheme(document.documentElement.getAttribute("data-theme") || initialTheme);
  });
})();
