// Funcionalidad para los botones de navegación
document.querySelectorAll('.nav-btn').forEach(button => {
    button.addEventListener('click', function() {
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        this.classList.add('active');
    });
});

// Funcionalidad para los botones de contacto
document.querySelectorAll('.contact-btn').forEach(button => {
    button.addEventListener('click', function() {
        const card = this.closest('.vehicle-card');
        const title = card.querySelector('.vehicle-title').textContent;
        const price = card.querySelector('.vehicle-price').textContent;
        alert(`Contactando al propietario para: ${title} (${price})`);
    });
});

// Funcionalidad para los elementos del sidebar
document.querySelectorAll('.sidebar-item').forEach(item => {
    item.addEventListener('click', function() {
        const title = this.querySelector('.item-title').textContent;
        console.log(`Elemento seleccionado: ${title}`);
    });
});

// Funcionalidad para los elementos de usuario
document.querySelectorAll('.user-item').forEach(item => {
    item.addEventListener('click', function() {
        const name = this.querySelector('.user-name').textContent;
        console.log(`Usuario seleccionado: ${name}`);
    });
});

// Funcionalidad para las acciones rápidas
document.querySelectorAll('.action-btn').forEach(button => {
    button.addEventListener('click', function() {
        const action = this.querySelector('.action-text').textContent;
        alert(`Acción: ${action}`);
    });
});

// Funcionalidad para el botón de publicar vehículo
document.querySelector('.publish-btn').addEventListener('click', function() {
    alert('Redirigiendo a formulario para publicar vehículo');
});

// Funcionalidad para la búsqueda
const searchInput = document.querySelector('.search-input input');
searchInput.addEventListener('keyup', function(e) {
    if (e.key === 'Enter') {
        performSearch();
    }
});

// Funcionalidad para el filtro de ubicación
const locationSelect = document.querySelector('.location-select select');
locationSelect.addEventListener('change', function() {
    performSearch();
});

function performSearch() {
    const searchTerm = searchInput.value;
    const location = locationSelect.value;

    console.log(`Buscando: "${searchTerm}" en ubicación: ${location}`);
    // Aquí iría la lógica para filtrar los resultados
}

// Funcionalidad para el filtro de ordenamiento
const sortSelect = document.querySelector('.sort-select select');
sortSelect.addEventListener('change', function() {
    console.log(`Ordenando por: ${this.value}`);
    // Aquí iría la lógica para ordenar los resultados
});

// Funcionalidad para el menú móvil (si se implementa)
document.addEventListener('DOMContentLoaded', function() {
    // Aquí se puede agregar la funcionalidad para el menú móvil
    console.log('Página cargada correctamente');
});
