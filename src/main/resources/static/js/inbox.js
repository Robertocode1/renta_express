$(document).ready(function() {
    if (typeof miIdDeUsuario === 'undefined') {
        //console.log("inbox.js ignorado: miIdDeUsuario no definido.");
        return;
    }

    // --- 1. GLOBALES ---
    let stompClient = null;
    let idDelOtroUsuario = null; // El chat que está activo AHORA
    const $chatWindow = $('#chat-window-container');
    const $chatPlaceholder = $('#chat-placeholder');
    const $chatBody = $('#caja-de-chat');
    const $chatInput = $('#input-mensaje');
    const $chatButton = $('#boton-enviar');
    const $chatHeaderNombre = $('#chat-header-nombre');
    const $chatHeaderFoto = $('#chat-header-foto');
    const $inboxLista = $('#inbox-lista'); // Guardamos la lista

    // 'miIdDeUsuario' es global (del script th:inline)

    // --- 2. FUNCIONES DE UI ---

    function scrollAlFondo() {
        $chatBody.scrollTop($chatBody[0].scrollHeight);
    }

    function mostrarMensaje(mensaje) {
        let remitenteId = (mensaje.remitente && mensaje.remitente.id) ? mensaje.remitente.id : mensaje.remitenteId;
        const tipo = (remitenteId === miIdDeUsuario) ? 'enviado' : 'recibido';
        const justificado = (tipo === 'enviado') ? 'justify-content-end' : 'justify-content-start';
        const burbujaClase = (tipo === 'enviado') ? 'chat-bubble-enviado' : 'chat-bubble-recibido';
        const textoColor = (tipo === 'enviado') ? 'text-white-50' : 'text-muted';

        const fecha = new Date(mensaje.timestamp);
        const hora = fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });

        const htmlDelMensaje = `
            <div class="d-flex ${justificado}">
                <div class="${burbujaClase}">
                    <p class="mb-1">${mensaje.contenido}</p>
                    <span class="small ${textoColor} d-block text-end">${hora}</span>
                </div>
            </div>
        `;
        $chatBody.append(htmlDelMensaje);
    }

    // --- ¡¡NUEVA FUNCIÓN!! ---
    /**
     * Actualiza un item en la lista de inbox cuando llega un mensaje nuevo
     * de un chat que NO está activo.
     */
    function actualizarInboxItem(mensaje) {
        // El remitente del mensaje es el "otroUsuario" en este contexto
        const remitenteId = (mensaje.remitente && mensaje.remitente.id) ? mensaje.remitente.id : mensaje.remitenteId;

        // 1. Buscar el item de ese usuario en la lista
        const $inboxItem = $('.inbox-item[data-usuario-id="' + remitenteId + '"]');

        if ($inboxItem.length > 0) { // Si ya tenemos una conversación con él...

            // 2. Formatear la hora
            const fecha = new Date(mensaje.timestamp);
            const hora = fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });

            // 3. Actualizar el contenido del item
            // Usamos .html() para el nuevo span, y .text() para la hora
            $inboxItem.find('p').html('<span class="text-primary fw-bold">Nuevo: </span>' + mensaje.contenido);
            $inboxItem.find('small').text(hora);

            // 4. Añadir una clase "no leído"
            // (Asegúrate de añadir esta clase .inbox-item-unread a tu CSS)
            $inboxItem.addClass('inbox-item-unread');

            // 5. ¡Moverlo al principio de la lista!
            $inboxItem.prependTo($inboxLista);

        } else {
            // Es un chat de alguien con quien NUNCA has hablado.
            // Esto es más complejo (requeriría crear un nuevo item de inbox)
            // Por ahora, solo lo logueamos.
            console.log("Mensaje recibido de un usuario nuevo:", remitenteId);
            // (En el futuro, podrías hacer una llamada AJAX aquí para
            // buscar los datos de ese usuario y crear el item de inbox)
        }
    }


    // --- 3. LÓGICA DE WEBSOCKET (¡ACTUALIZADA!) ---

    function conectarYSuscribir() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Iniciando conexión WebSocket...");

        const socket = new SockJS('/ws-chat');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({},
            function(frame) {
                console.log('¡Conectado! ' + frame);

                // Suscribirse a MI cola privada
                stompClient.subscribe('/queue/private.' + miIdDeUsuario, function(payload) {
                    const mensaje = JSON.parse(payload.body);
                    console.log("Mensaje recibido:", mensaje);

                    let remitenteId = (mensaje.remitente && mensaje.remitente.id) ? mensaje.remitente.id : mensaje.remitenteId;

                    // --- ¡¡ESTA ES LA LÓGICA MEJORADA!! ---
                    if (remitenteId === idDelOtroUsuario) {
                        // Caso 1: El mensaje es del chat que estoy viendo.
                        // Lo pinto en la ventana.
                        mostrarMensaje(mensaje);
                        scrollAlFondo();
                    } else {
                        // Caso 2: El mensaje es de OTRO chat.
                        // Actualizo la lista de la izquierda.
                        actualizarInboxItem(mensaje);
                    }
                });
            },
            function(error) {
                console.error('Error de conexión WebSocket: ' + error);
            }
        );
    }

    // --- 4. LÓGICA DE CARGA (Sin cambios) ---

    function cargarHistorial(usuarioId) {
        console.log("Cargando historial para:", usuarioId);
        idDelOtroUsuario = usuarioId; // Setea el ID global
        $chatBody.empty().append("<p>Cargando mensajes...</p>");

        $.ajax({
            url: '/chat/historial/' + idDelOtroUsuario,
            method: 'GET',
            dataType: 'json',
            success: function(historial) {
                $chatBody.empty();
                historial.forEach(function(mensaje) {
                    mostrarMensaje(mensaje);
                });
                scrollAlFondo();
                $chatInput.prop('disabled', false);
                $chatButton.prop('disabled', false);
            },
            error: function(xhr) {
                $chatBody.empty().append("<p>Error al cargar el chat.</p>");
                $chatInput.prop('disabled', true);
                $chatButton.prop('disabled', true);
            }
        });
    }

    // --- 5. EVENT LISTENERS (¡ACTUALIZADO!) ---

    // Al hacer clic en un item de la bandeja de entrada
    $inboxLista.on('click', '.inbox-item', function() {
        const $item = $(this);

        // --- ¡¡LÍNEA AÑADIDA!! ---
        // Al hacer clic, quitamos la marca de "no leído"
        $item.removeClass('inbox-item-unread');

        const nuevoId = $item.data('usuario-id');
        const nombre = $item.data('usuario-nombre');
        const foto = $item.data('usuario-foto');

        $('.inbox-item').removeClass('active');
        $item.addClass('active');

        $chatWindow.removeClass('d-none');
        $chatPlaceholder.addClass('d-none');

        $chatHeaderNombre.text(nombre);
        $chatHeaderFoto.attr('src', foto);

        // Si es un chat diferente, recargamos.
        // Si es el mismo, no hacemos nada (evita recarga innecesaria)
        if (nuevoId !== idDelOtroUsuario) {
            cargarHistorial(nuevoId);
        }
    });

    // Al enviar un mensaje (Sin cambios)
    $('#formulario-chat').on('submit', function(e) {
        e.preventDefault();
        const contenido = $chatInput.val();

        if (contenido && stompClient && idDelOtroUsuario) {
            const chatMessage = {
                contenido: contenido,
                remitenteId: miIdDeUsuario,
                destinatarioId: idDelOtroUsuario
            };
            stompClient.send("/app/chat.privado", {}, JSON.stringify(chatMessage));

            const mensajeOptimista = {
                contenido: contenido,
                remitente: { id: miIdDeUsuario },
                timestamp: new Date().toISOString()
            };
            mostrarMensaje(mensajeOptimista);
            scrollAlFondo();
            $chatInput.val('');
        }
    });

    // --- 6. ARRANQUE (Sin cambios) ---
    conectarYSuscribir();

});