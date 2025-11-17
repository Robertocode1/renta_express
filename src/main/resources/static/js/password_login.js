$(function () {
    //Funcion para validar el correo en la vista registro
    $("#correo").on("input", function () {

        $.ajax({
            url: "/Login/ValidarCorreo", // Ruta del controlador
            method: "POST",
            dataType: "text", //json o text
            data: { correo: $(this).val() }, //correo es la variable y this porque es del mismo objeto
            success: function (resp) {
                if (resp === "1") {
                    $("#correo").addClass("is-invalid text-danger");
                    $("#correo").removeClass("is-valid");
                    $("#btn").prop("disabled", true);

                }
                else {

                    $("#correo").removeClass("is-invalid text-danger");
                    $("#correo").addClass("is-valid text-success");
                    $("#btn").prop("disabled", false);
                }
            },
            error: function (jqHXR, textStatus, errorThrown) {
                console.log(jqHXR.responseText || textStatus, errorThrown);
            }
        });
    });

    //Funcion para validar el correo para cambiar password
    $("#correoPass").on("input", function () {

        $.ajax({
            url: "/Login/ValidarCorreo", // Ruta del controlador
            method: "POST",
            dataType: "text", //json o text
            data: { correo: $(this).val() }, //correo es la variable y this porque es del mismo objeto
            success: function (resp) {
                if (resp === "1") {

                    $("#correoPass").addClass("is-valid");
                    $("#correoPass").removeClass("is-invalid");
                    $("#btn").prop("disabled", false);

                }
                else {

                    $("#correoPass").addClass("is-invalid text-danger");
                    $("#correoPass").removeClass("is-valid");
                    $("#btn").prop("disabled", true);
                }
            },
            error: function (jqHXR, textStatus, errorThrown) {
                console.log(jqHXR.responseText || textStatus, errorThrown);
            }
        });
    });

    // Funcion para asignar el id al form
    $('#cambiarContrasenaModal').on('show.bs.modal', function(event) {
        // Obtiene el botón que lo activó
        const boton = $(event.relatedTarget);

        const usuarioId = boton.data('usuario-id');

        // Buscamos el <form> dentro del modal que se está abriendo.
        $(this).find('form').data('usuario-id', usuarioId);
        $(this).find('#cambiarPassId').val(usuarioId);

    });

    // Este código se define una sola vez y espera a que el input #contrasenaActual pierda el foco.
    $('#contrasenaActual').on('blur', function() {
        const passwordIngresada = $(this).val();
        const usuarioId = $('#formCambiarContrasena').data('usuario-id');
        const matchText = document.getElementById('contrasenaMatch');

        // Si no hay contraseña o ID, no hacemos nada
        if (!passwordIngresada || !usuarioId) {
            return;
        }

        $.ajax({
            url: "/usuario/verificarPassword",
            method: "POST",
            data:{
                id: usuarioId,
                password: passwordIngresada },
            success: function(response) {
                matchText.innerHTML = '';
                $('#contrasenaActual').removeClass('is-invalid').addClass('is-valid');
            },
            error: function(jqXHR) {
                console.error("La contraseña es incorrecta.");
                const errorMsg = jqXHR.responseJSON?.mensaje || "Contraseña incorrecta.";
                $('#contrasenaActual').removeClass('is-valid').addClass('is-invalid');
                matchText.innerHTML = '<i class="fas fa-times text-danger me-1"></i>Contraseña actual incorrecta!';
                matchText.className = 'form-text small text-danger';
            }
        });
    });

});

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

// funcion para recuperar contrasenia por medio del correo
// Espera a que todo el HTML esté cargado
$(document).ready(function() {
    $('#resetForm').on('submit', function(e) {
        e.preventDefault(); // Evita el envío tradicional

        const $form = $(this);
        const $btn = $('#submitBtn');
        const $spinner = $btn.find('.spinner-border');
        const $btnText = $btn.find('.btn-text');

        // Estado de carga
        $btn.prop('disabled', true);
        $spinner.removeClass('d-none');
        $btnText.text('Enviando...');

        $.ajax({
            url: $form.attr('action'),
            method: 'POST',
            data: $form.serialize(),
            success: function(response) {
                // Éxito: cerrar resetModal y abrir successModal
                $('#resetModal').modal('hide');
                $('#successModal').modal('show');
            },
            error: function(xhr) {
                // Error: extraer mensaje y mostrar errorModal
                let message = "Ocurrió un error inesperado.";
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }

                $('#errorMessage').text(message);
                const $resetModal = $('#resetModal');
                $resetModal.one('hidden.bs.modal', function () {
                    $('#errorModal').modal('show');
                });
                $resetModal.modal('hide');
            },
            complete: function() {
                // Siempre restablecer botón y formulario
                $btn.prop('disabled', false);
                $spinner.addClass('d-none');
                $btnText.text('Enviar enlace de recuperación');
                $form[0].reset();
            }
        });
    });
    //Ocultar error modal
    $(document).on('click', '#retryResetButton', function() {
        $(this).blur();
        setTimeout(function() {
            var $errorModal = $('#errorModal');
            var $resetModal = $('#resetModal');
            $errorModal.one('hidden.bs.modal', function() {
                $resetModal.modal('show');
            });
            $errorModal.modal('hide');

        }, 0);
    });
    //ocultar reset modal
    $(document).on('click', '.close-reset-modal', function() {
        $(this).blur();
        setTimeout(function() {
            $('#resetModal').modal('hide');

        }, 0);
    });
    //ocultar login modal
    $(document).on('click', '.btn-reset-pw', function() {
        $(this).blur();
        setTimeout(function() {
            $('#loginModal').modal('hide');

        }, 0);
    });
});
