
//---------------------modal agregar usuario o registrarse----------------

document.getElementById('publicarVehiculoModal').addEventListener('show.bs.modal', function () {
    inicializarCaracteristicas();
});

function inicializarCaracteristicas() {
    const checkboxes = document.querySelectorAll('.btn-check'); // o .caracteristica-check

    checkboxes.forEach(chk => {
        chk.replaceWith(chk.cloneNode(true));
    });

    const nuevosCheckboxes = document.querySelectorAll('.btn-check');

    nuevosCheckboxes.forEach(chk => {
        chk.addEventListener('change', function () {
            const label = document.querySelector(`label[for="${this.id}"]`);

            if (this.checked) {
                label.classList.remove('btn-outline-secondary');
                label.classList.add('btn-primary');
            } else {
                label.classList.remove('btn-primary');
                label.classList.add('btn-outline-secondary');
            }

            actualizarContadorCaracteristicas();
        });

        // Estado inicial
        const label = document.querySelector(`label[for="${chk.id}"]`);
        if (chk.checked) {
            label.classList.remove('btn-outline-secondary');
            label.classList.add('btn-primary');
        }
    });

    actualizarContadorCaracteristicas();
}

// Función para actualizar contador de caracteristicas
function actualizarContadorCaracteristicas() {
    const totalSeleccionadas = document.querySelectorAll('.btn-check:checked').length;
    const contador = document.getElementById('contadorCaracteristicas');
    if (contador) {
        contador.textContent = `(${totalSeleccionadas} seleccionadas)`;
    }
}

//Funciones para las previsualizaciones de imagenes de agregar publicacion
document.addEventListener('DOMContentLoaded', function () {
    const MAX_IMAGES = 5;
    const imageUploadInput = document.getElementById('imageUpload');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const errorContainer = document.getElementById('imageError');
    const publishForm = document.getElementById('publishForm');
    const modalElement = document.getElementById('publicarVehiculoModal');
    const modalLabel = document.getElementById('publicarVehiculoModalLabel');

    // --- NUESTRA FUENTE ÚNICA DE VERDAD ---
    // Este array gestionará tanto archivos nuevos como imágenes existentes.
    // item = { type: 'new', file: FileObject }
    // item = { type: 'existing', url: '...', id: 123 }
    let previewItems = [];

    // --- LÓGICA DE RENDERIZADO (MODIFICADA) ---

    /**
     * Renderiza las 5 ranuras (slots) basándose en el array `previewItems`.
     * Esta función ahora sabe cómo dibujar tanto archivos nuevos como existentes.
     */
    function renderPreviewSlots() {
        previewContainer.innerHTML = ''; // Limpia el contenedor

        for (let i = 0; i < MAX_IMAGES; i++) {
            const slot = document.createElement('div');
            slot.classList.add('preview-slot'); // Asegúrate de tener el CSS para .preview-slot
            const item = previewItems[i];

            if (item) {
                // Hay un item (nuevo o existente)
                const img = document.createElement('img');

                // Asigna la URL de la imagen
                img.src = (item.type === 'new')
                    ? URL.createObjectURL(item.file)
                    : item.url;

                if (item.type === 'new') {
                    // Libera memoria para los archivos nuevos
                    img.onload = () => URL.revokeObjectURL(img.src);
                }

                const removeBtn = document.createElement('button');
                removeBtn.classList.add('remove-img-btn'); // Asegúrate de tener el CSS
                removeBtn.innerHTML = '&times;';
                removeBtn.type = 'button';
                removeBtn.onclick = () => removeImage(i);

                slot.appendChild(img);
                slot.appendChild(removeBtn);

                if (i === 0) {
                    const mainTag = document.createElement('span');
                    mainTag.classList.add('main-tag'); // CSS para la etiqueta "Principal"
                    mainTag.textContent = 'Principal';
                    slot.appendChild(mainTag);
                }

            } else {
                // Ranura vacía
                const icon = document.createElement('i');
                icon.className = 'fas fa-camera icon';
                slot.appendChild(icon);
            }
            previewContainer.appendChild(slot);
        }
    }

    /**
     * Maneja la selección de nuevos archivos.
     */
    function handleFileSelect(event) {
        errorContainer.textContent = '';
        const newFiles = Array.from(event.target.files);

        if (previewItems.length + newFiles.length > MAX_IMAGES) {
            errorContainer.textContent = `Error: No puedes subir más de ${MAX_IMAGES} imágenes en total.`;
            imageUploadInput.value = '';
            return;
        }

        // Añade los nuevos archivos al array con el formato correcto
        newFiles.forEach(file => {
            previewItems.push({ type: 'new', file: file });
        });

        updateFileInput();
        renderPreviewSlots();
    }

    /**
     * Elimina una imagen (nueva O existente) del array.
     */
    function removeImage(index) {
        // Elimina el item del array y lo captura
        const removedItem = previewItems.splice(index, 1)[0];

        // --- ¡CLAVE PARA EDITAR! ---
        // Si el item era 'existing', tenemos que decirle al backend que lo borre.
        if (removedItem && removedItem.type === 'existing') {
            // Creamos un input oculto con el ID de la imagen a eliminar
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'imagenesAEliminar'; // Tu backend debe buscar este parámetro
            hiddenInput.value = removedItem.id;
            publishForm.appendChild(hiddenInput);
        }

        updateFileInput(); // Sincroniza el input
        renderPreviewSlots(); // Vuelve a dibujar los slots
    }

    /**
     * Sincroniza el <input type="file"> con nuestro array `previewItems`.
     * Solo añade los archivos de tipo 'new', ya que los 'existing' ya están en el servidor.
     */
    function updateFileInput() {
        const dataTransfer = new DataTransfer();
        previewItems.forEach(item => {
            if (item.type === 'new') {
                dataTransfer.items.add(item.file);
            }
        });
        imageUploadInput.files = dataTransfer.files;
    }

    // Listener inicial para el input de archivos
    imageUploadInput.addEventListener('change', handleFileSelect);

    // --- LÓGICA PARA CONTADORES (sin cambios) ---
    const descriptionTextarea = document.getElementById('descripcion');
    const charCounter = document.getElementById('charCounter');
    descriptionTextarea.addEventListener('input', () => {
        charCounter.textContent = descriptionTextarea.value.length;
    });

    const featureContainer = document.getElementById('caracteristicasContainer');
    const featureCounter = document.getElementById('featureCounter');
    featureContainer.addEventListener('change', () => {
        const checkedCount = featureContainer.querySelectorAll('input[type="checkbox"]:checked').length;
        featureCounter.textContent = checkedCount;
    });

    // --- LÓGICA PARA RESETEAR EL MODAL (¡MUY IMPORTANTE!) ---
    // Esto limpia el modal CADA VEZ que se cierra,
    // preparándolo para "Crear" o para ser llenado por "Editar".
    modalElement.addEventListener('hidden.bs.modal', function () {
        publishForm.reset(); // Resetea campos
        previewItems = []; // Vacía el array de imágenes
        updateFileInput(); // Limpia el input de archivos
        renderPreviewSlots(); // Dibuja los slots vacíos

        // Limpia contadores
        charCounter.textContent = '0';
        featureCounter.textContent = '0';
        errorContainer.textContent = '';

        // Limpia inputs ocultos de borrado
        publishForm.querySelectorAll('input[name="imagenesAEliminar"]').forEach(el => el.remove());

        // Resetea al modo "Crear" por defecto
        modalLabel.innerHTML = '<i class="fas fa-car me-2"></i>Publicar tu Vehículo';
        publishForm.setAttribute('action', '/publicaciones/agregar'); // Acción original
        publishForm.querySelector('input[name="_method"]')?.remove(); // Quita el _method PUT

        // Resetea selects dependientes (si los tienes)
        document.getElementById('municipio').innerHTML = '<option value="" disabled selected>Selecciona...</option>';
        document.getElementById('municipio').disabled = true;
    });

    // Inicializar vistas al cargar
    renderPreviewSlots();

    // Hacemos que estas funciones sean "globales" dentro del scope del modal
    // para que tu AJAX pueda usarlas.
    window.modalHelpers = {
        populateImages: function(imagenes) {
            previewItems = []; // Limpia por si acaso
            if (imagenes && imagenes.length) {
                imagenes.forEach(img => {
                    // Asume que tu objeto 'img' tiene 'id' y 'urlImagen'
                    previewItems.push({ type: 'existing', url: img.urlImagen, id: img.id });
                });
            }
            renderPreviewSlots();
            updateFileInput(); // Sincroniza (el input estará vacío de archivos, es correcto)
        },
        resetImageErrors: function() {
            errorContainer.textContent = '';
            publishForm.querySelectorAll('input[name="imagenesAEliminar"]').forEach(el => el.remove());
        }
    };
});

// Funcionalidades para hacer dinámico el modal agregar y actualizar
$(document).ready(function() {

    // --- 1. LISTENERS DE FOTO (EL LUGAR CORRECTO) ---
    // Se registran UNA VEZ cuando la página está lista.

    // Previsualización de imagen desde archivo
    const fotoInput = document.getElementById('fotoInput');
    const fotoPreview = document.getElementById('fotoPreview');
    const fotoUrl = document.getElementById('fotoUrl'); // Asumiendo que existe
    const defaultFoto = '/images/usuarios/default_user_img.jpg';

    if (fotoInput) {
        fotoInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    if (fotoPreview) {
                        fotoPreview.src = e.target.result;
                    }
                    if (fotoUrl) {
                        fotoUrl.value = ''; // Limpiar URL si se sube archivo
                    }
                }
                reader.readAsDataURL(file);
            }
        });
    }

    // Click en la imagen también abre el selector de archivos
    if (fotoPreview) {
        fotoPreview.addEventListener('click', function() {
            if (fotoInput) {
                fotoInput.click();
            }
        });
    }

    // --- 2. MANEJO DEL BOTÓN "EDITAR" ---
    $(document).on('click', '.btn-editar-usuario', function() {

        const usuarioId = $(this).data('usuario-id');

        $.ajax({
            url: '/usuario/' + usuarioId,
            method: 'GET',
            success: function(usuario) {
                const $modal = $('#agregarModal');
                const $form = $modal.find('form');

                // 1. Cambiar el título del modal y la acción del formulario
                $modal.find('#agregarModalLabel').text('Editar Cuenta');
                $form.attr('action', '/usuario/actualizar/' + usuarioId);
                $form.find('input[name="_method"]').remove(); // Limpia por si acaso
                $form.append('<input type="hidden" name="_method" value="put">');

                // 2. Rellenar los campos del formulario con los datos recibidos
                $modal.find('#nombre').val(usuario.nombre);
                $modal.find('#usuario').val(usuario.usuario);
                $modal.find('#email').val(usuario.email);
                $modal.find('#documento').val(usuario.documento);

                if (fotoUrl) {
                    $modal.find('#fotoUrl').val(usuario.foto);
                }

                if (usuario.rol.id) {
                    $modal.find('#rol').val(usuario.rol.id);
                } else {
                    $modal.find('#rol').prop('selectedIndex', 0);
                }

                // 3. Actualizar la vista previa de la imagen
                if (fotoPreview) {
                    if (usuario.foto) {
                        fotoPreview.src = usuario.foto;
                    } else {
                        fotoPreview.src = defaultFoto;
                    }
                }

                // 4. Finalmente, mostrar el modal
                $modal.modal('show');
            },
            error: function() {
                alert('Error: No se pudo cargar la información del usuario.');
            }
        });
    });

    // --- 3. MANEJO DEL BOTÓN "AGREGAR NUEVO" ---
    $('#btnAgregarUsuario').on('click', function() {
        const $modal = $('#agregarModal');
        const $form = $modal.find('form');

        // 1. Cambiar el título y la acción a sus valores originales
        $modal.find('#agregarModalLabel').text('Cuenta nueva');
        $form.attr('action', '/usuario/agregarUsuario');
        $form.find('input[name="_method"]').remove(); // Quita el campo PUT

        // 2. Limpiar todos los campos del formulario
        $form[0].reset();
        $modal.find('#rol').prop('selectedIndex', 0);

        // 3. Poner la imagen por defecto
        if (fotoPreview) {
            fotoPreview.src = defaultFoto;
        }
    });

});

//Funciones para el modal eliminar usuario
$(document).ready(function() {
    // Cuando el modal con id 'eliminarModal' está a punto de mostrarse...
    $('#eliminarModal').on('show.bs.modal', function(event) {
        // Obtiene el botón que lo activó
        var boton = $(event.relatedTarget);

        // Extrae el ID del atributo data-*
        var usuarioId = boton.data('usuario-id');

        // Encuentra el input '#deleteId' DENTRO del modal y establece su valor
        $(this).find('#eliminarUserId').val(usuarioId);
    });
});

//Funciones para el modal restablecer password
$(document).ready(function() {
    // Cuando el modal con id 'resetModal' está a punto de mostrarse...
    $('#resetPasswordModal').on('show.bs.modal', function(event) {
        // Obtiene el botón que lo activó
        var boton = $(event.relatedTarget);

        // Extrae el ID del atributo data-*
        var usuarioId = boton.data('usuario-id');

        // Encuentra el input '#resetUserId' DENTRO del modal y establece su valor
        $(this).find('#resetUserId').val(usuarioId);
    });
});

//Funciones para el modal responder
$(document).ready(function() {
    // Cuando el modal con id 'eliminarModal' está a punto de mostrarse...
    $('#responderModal').on('show.bs.modal', function(event) {
        // Obtiene el botón que lo activó
        var boton = $(event.relatedTarget);

        // Extrae el ID del atributo data-*
        var destinatario = boton.data('correo');
        var nombreCon = boton.data('nombre-contacto');
        var asunto = boton.data('asunto');
        var mensajeId = boton.data('mensaje-id');
        console.log("name:" + mensajeId);
        // Encuentras los input DENTRO del modal y establece su valor
        $(this).find('#destinatario').val(destinatario);
        $(this).find('#asuntoResp').val(asunto);
        $(this).find('#respLabel').text(nombreCon);
        $(this).find('#mensajeId').val(mensajeId);
    });
});

//Funcion para modal agregar/editar publicacion
// MANEJO DEL BOTÓN "EDITAR PUBLICACIÓN"
$(document).on('click', '.btn-editar-publicacion', function() {
    const publicacionId = $(this).data('publicacion-id');

    $.ajax({
        url: '/publicaciones/publicacionPorId/' + publicacionId,
        method: 'GET',
        success: function(publicacion) {

            // --- INICIO DE CAMBIOS --

            // 1. Limpiar el estado de las imágenes (errores, inputs de borrado)
            // La función 'hidden.bs.modal' ya reseteó el formulario, pero
            // hacemos esto por seguridad.
            if (window.modalHelpers) {
                window.modalHelpers.resetImageErrors();
            }

            // 2. Cambiar el título del modal y la acción del formulario
            $('#publicarVehiculoModalLabel').html('<i class="fas fa-car me-2"></i>Editar Publicación');
            $('#publishForm').attr('action', '/publicaciones/actualizar/' + publicacionId);
            $('#publishForm').find('input[name="_method"]').remove();
            $('#publishForm').append('<input type="hidden" name="_method" value="put">');

            // 3. Rellenar los campos del formulario
            $('#publishForm #categoria').val(publicacion.idCategoria.id);
            $('#publishForm #tituloAnuncio').val(publicacion.titulo);
            $('#publishForm #descripcion').val(publicacion.descripcion);
            $('#publishForm #precio').val(publicacion.precio);
            $('#publishForm #tipoPrecio').val(publicacion.idTipoPrecio.id);
            $('#publishForm #departamento').val(publicacion.idDepartamento.id);
            $('#publishForm #telefono').val(publicacion.telefono);
            $('#publishForm #municipio').val(publicacion.idMunicipio.id);
            // ... (Asegúrate de cargar municipios si es necesario) ...

            // 4. Marcar características
            // (Tu lógica original 'marcarCaracteristicas' debería funcionar si
            // primero desmarca todas y luego marca las que vienen)
            $('#caracteristicasContainer input[type="checkbox"]').prop('checked', false); // Desmarca todas
            if (publicacion.caracteristicas) {
                publicacion.caracteristicas.forEach(car => {
                    $(`#caracteristica-${car.id}`).prop('checked', true);
                });
            }
            // Actualiza el contador de features
            const checkedCount = $('#caracteristicasContainer input[type="checkbox"]:checked').length;
            $('#featureCounter').text(checkedCount);


            // 5. Previsualizar imágenes (Usando el nuevo helper)
            // ¡ESTO REEMPLAZA TU previsualizarImagenes()!
            if (window.modalHelpers) {
                window.modalHelpers.populateImages(publicacion.imagenes);
            }

            // 6. Actualizar contador de descripción
            $('#charCounter').text($('#descripcion').val().length);

            // 7. Finalmente, mostrar el modal
            $('#publicarVehiculoModal').modal('show');

            // --- FIN DE CAMBIOS ---

        },
        error: function(xhr, status, error) {
            console.error("Error en la solicitud:", status, error);
            alert('Error: No se pudo cargar la información de la publicación.');
        }
    });
});

// Función para cargar municipios según departamento seleccionado
function cargarMunicipios(departamentoId, municipioSeleccionadoId = null) {
    $.ajax({
        url: '/municipios/' + departamentoId,
        method: 'GET',
        success: function(municipios) {
            const municipioSelect = $('#publishForm #municipio');
            municipioSelect.empty().append('<option value="" disabled selected>Selecciona...</option>');
            municipios.forEach(municipio => {
                const selected = municipioSeleccionadoId && municipio.id === municipioSeleccionadoId ? 'selected' : '';
                municipioSelect.append(`<option value="${municipio.id}" ${selected}>${municipio.nombre}</option>`);
            });
        },
        error: function() {
            alert('Error: No se pudieron cargar los municipios.');
        }
    });
}

// Función para marcar características seleccionadas
function marcarCaracteristicas(caracteristicasSeleccionadas) {
    // Desmarcar todas las checkboxes
    $('#publishForm input[name="caracteristicas"]').prop('checked', false);

    // Marcar las que vienen en la publicación
    caracteristicasSeleccionadas.forEach(caracteristica => {
        $(`#publishForm input[name="caracteristicas"][value="${caracteristica.id}"]`).prop('checked', true);
    });
}


