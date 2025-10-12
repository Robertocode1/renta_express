//funcion para modal login
document.getElementById('togglePassword').addEventListener('click', function() {
    const passwordInput = document.getElementById('password');
    const icon = this.querySelector('i');

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        icon.className = 'ri-eye-off-line';
    } else {
        passwordInput.type = 'password';
        icon.className = 'ri-eye-line';
    }
});

// Script para mostrar/ocultar contraseña en el modal de cambiar contraseña
document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', function() {
        const targetId = this.getAttribute('data-target');
        const passwordInput = document.getElementById(targetId);
        const icon = this.querySelector('i');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            icon.className = 'fas fa-eye-slash';
        } else {
            passwordInput.type = 'password';
            icon.className = 'fas fa-eye';
        }
    });
});

// Validación de coincidencia de contraseñas en el modal de cambiar contraseña
document.getElementById('confirmarContrasena').addEventListener('input', function() {
    const nuevaContrasena = document.getElementById('nuevaContrasena').value;
    const confirmarContrasena = this.value;
    const matchText = document.getElementById('contrasenaMatch');

    if (confirmarContrasena) {
        if (nuevaContrasena === confirmarContrasena) {
            matchText.innerHTML = '<i class="fas fa-check text-success me-1"></i>Las contraseñas coinciden';
            matchText.className = 'form-text small text-success';
        } else {
            matchText.innerHTML = '<i class="fas fa-times text-danger me-1"></i>Las contraseñas no coinciden';
            matchText.className = 'form-text small text-danger';
        }
    } else {
        matchText.innerHTML = '';
    }
});

//---------------------modal agregar usuario o registrarse----------------

// Previsualización de imagen desde archivo
document.getElementById('fotoInput').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('fotoPreview').src = e.target.result;
            document.getElementById('fotoUrl').value = ''; // Limpiar URL si se sube archivo
        }
        reader.readAsDataURL(file);
    }
});

// Previsualización de imagen desde URL
document.getElementById('fotoUrl').addEventListener('input', function(e) {
    const url = e.target.value;
    if (url) {
        document.getElementById('fotoPreview').src = url;
        document.getElementById('fotoInput').value = ''; // Limpiar file input si se usa URL
    } else {
        document.getElementById('fotoPreview').src = 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png';
    }
});

// Click en la imagen también abre el selector de archivos
document.getElementById('fotoPreview').addEventListener('click', function() {
    document.getElementById('fotoInput').click();
});

// Método para asignar id al modal
document.addEventListener('DOMContentLoaded', function() {
    const deleteModal = document.getElementById('eliminarModal');

    deleteModal.addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget; // Botón que activó el modal
        const userId = button.getAttribute('data-user-id');

        if (userId) {
            document.getElementById('deleteId').value = userId;
            console.log('ID establecido para eliminar:', userId);
        }
    });
});

// Método para asignar id al modal
document.addEventListener('DOMContentLoaded', function() {
    const deleteModal = document.getElementById('cambiarContrasenaModal');

    deleteModal.addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget; // Botón que activó el modal
        const userId = button.getAttribute('data-user-id');

        if (userId) {
            document.getElementById('password_id').value = userId;
            console.log('ID establecido para eliminar:', userId);
        }
    });
});















