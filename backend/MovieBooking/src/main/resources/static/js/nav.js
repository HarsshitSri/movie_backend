(function initNav() {
  const path = window.location.pathname.split("/").pop() || "index.html";
  const file = path === "" || path === "/" ? "index.html" : path;

  document.querySelectorAll(".nav a[data-nav]").forEach((link) => {
    if (link.getAttribute("data-nav") === file) {
      link.classList.add("active");
    }
  });

  const state = document.getElementById("auth-state");
  const logoutBtn = document.getElementById("logout-btn");

  if (state) {
    if (Auth.isLoggedIn()) {
      state.textContent = Auth.email() || "Signed in";
    } else {
      state.textContent = "Guest";
    }
  }

  if (logoutBtn) {
    logoutBtn.style.display = Auth.isLoggedIn() ? "inline-flex" : "none";
    logoutBtn.addEventListener("click", () => {
      Auth.clear();
      window.location.href = "/index.html";
    });
  }
})();
