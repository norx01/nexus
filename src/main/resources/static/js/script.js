document.addEventListener("DOMContentLoaded", function () {
    const sidebar = document.getElementById("sidebar");
    const openSidebar = document.getElementById("openSidebar");
    const closeSidebar = document.getElementById("closeSidebar");
    const overlay = document.getElementById("overlay");

    if (!sidebar || !openSidebar || !closeSidebar || !overlay) {
        console.error("Faltan elementos del sidebar.");
        return;
    }

    function toggleSidebar() {
        sidebar.classList.toggle("-translate-x-full");
        overlay.classList.toggle("hidden");
    }

    function closeMenu() {
        sidebar.classList.add("-translate-x-full");
        overlay.classList.add("hidden");
    }

    openSidebar.addEventListener("click", toggleSidebar);
    closeSidebar.addEventListener("click", closeMenu);
    overlay.addEventListener("click", closeMenu);
});