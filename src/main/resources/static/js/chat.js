$(document).ready(function() {

    if (typeof idDelOtroUsuario === 'undefined' || typeof miIdDeUsuario === 'undefined') {
        //console.warn("chat.js ignorado: variables no definidas.");
        return;
    }

    // --- 1. CONFIGURACIÓN INICIAL ---

    // IDs que Thymeleaf debe "quemar" en el HTML
    // Necesitarás una forma de pasar estos datos desde tu modelo.
    // Ejemplo: <script th:inline="javascript">
    //            const miIdDeUsuario = [[${session.usuario.id}]];
    //            const idDelOtroUsuario = [[${otroUsuario.id}]];
    //          </script>


    // El cliente STOMP que vivirá mientras la página esté abierta
    let stompClient = null;

    // Elementos de la UI
    const $chatBody = $('#caja-de-chat');
    const $chatForm = $('#formulario-chat');
    const $chatInput = $('#input-mensaje');

    // --- 2. FUNCIONES DE UI (PINTAR Y MOVER) ---

    /**
     * Mueve el scroll de la caja de chat hasta el fondo.
     */
    function scrollAlFondo() {
        $chatBody.scrollTop($chatBody[0].scrollHeight);
    }

    /**
     * Pinta un objeto de mensaje en la caja de chat.
     * @param {object} mensaje - El objeto Mensaje (de la BD)
     */
    function mostrarMensaje(mensaje) {
        // Determina si el mensaje es 'enviado' (por mí) o 'recibido' (por el otro)
        // OJO: El historial (AJAX) trae "remitente.id", el WebSocket puede traer "remitenteId"
        // Vamos a estandarizarlo.
        let remitenteId = (mensaje.remitente && mensaje.remitente.id) ? mensaje.remitente.id : mensaje.remitenteId;

        const tipo = (remitenteId === miIdDeUsuario) ? 'enviado' : 'recibido';

        // Formatear el timestamp (simple, puedes usar Moment.js si quieres)
        const fecha = new Date(mensaje.timestamp);
        const hora = fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });

        // Crea el HTML del mensaje
        const htmlDelMensaje = `
            <div class="mensaje-wrapper">
                <div class="mensaje ${tipo}">
                    <p>${mensaje.contenido}</p>
                    <span class="timestamp">${hora}</span>
                </div>
            </div>
        `;

        $chatBody.append(htmlDelMensaje);
    }

    // --- 3. LÓGICA DE WEBSOCKET (CONEXIÓN Y RECEPCIÓN) ---

    /**
     * Se conecta al endpoint de WebSocket y se suscribe a la cola privada.
     */
    function conectar() {
        console.log("Iniciando conexión WebSocket...");

        // 1. Crear la conexión (usando el endpoint del Paso 6)
        const socket = new SockJS('/ws-chat');
        stompClient = Stomp.over(socket);
        stompClient.debug = null; // (Desactiva el log molesto en la consola)

        // 2. Conectar
        stompClient.connect({},

            // 2a. Éxito de la conexión
            function(frame) {
                console.log('¡Conectado! ' + frame);

                // 3. Suscribirse a la cola privada (Paso 7)
                // Escuchará en "/queue/private.123" (si miIdDeUsuario es 123)
                stompClient.subscribe('/queue/private.' + miIdDeUsuario, function(payload) {

                    // 5. ¡MENSAJE RECIBIDO!
                    const mensaje = JSON.parse(payload.body);
                    console.log("Mensaje recibido:", mensaje);

                    mostrarMensaje(mensaje);
                    scrollAlFondo();
                });
            },

            // 2b. Error de conexión
            function(error) {
                console.error('Error de conexión WebSocket: ' + error);
                // (Aquí podrías mostrar un mensaje de error en la UI)
            }
        );
    }

    // --- 4. LÓGICA DE HISTORIAL (AJAX) ---

    /**
     * Carga el historial de chat (Paso 4) antes de conectar el WebSocket.
     */
    function cargarHistorial() {
        console.log("Cargando historial...");

        $chatBody.empty().append("<p>Cargando mensajes...</p>"); // Limpia la caja

        $.ajax({
            url: '/chat/historial/' + idDelOtroUsuario, // El endpoint del Paso 4
            method: 'GET',
            dataType: 'json',
            success: function(historial) {

                // --- AÑADE ESTA LÍNEA ---
                console.log("Tipo de 'historial':", typeof historial);
                // --- FIN DE LÍNEA A AÑADIR ---

                console.log("Historial cargado:", historial);

                $chatBody.empty(); // Limpia el "Cargando..."

                // Pinta cada mensaje del historial
                historial.forEach(function(mensaje) {
                    mostrarMensaje(mensaje);
                });

                // Mueve el scroll al fondo DESPUÉS de pintar todo
                scrollAlFondo();

                // ¡AHORA SÍ! Conecta el WebSocket
                conectar();
            },
            error: function(xhr) {
                console.error("Error al cargar historial:", xhr.responseText);
                $chatBody.empty().append("<p>Error al cargar el chat.</p>");
            }
        });
    }

    // --- 5. LÓGICA DE ENVÍO (FORMULARIO) ---

    $chatForm.on('submit', function(e) {
        e.preventDefault(); // ¡Evita que la página se recargue!

        const contenido = $chatInput.val();

        if (contenido && stompClient) {
            // 1. Construir el DTO (Paso 7a)
            const chatMessage = {
                contenido: contenido,
                remitenteId: miIdDeUsuario,
                destinatarioId: idDelOtroUsuario
            };

            // 2. Enviar el mensaje al controlador (Paso 7b)
            // (El destino /app/chat.privado)
            stompClient.send("/app/chat.privado", {}, JSON.stringify(chatMessage));

            // 3. (Opcional pero recomendado) Mostrar mi propio mensaje al instante
            // No esperamos a que el servidor nos lo devuelva.
            const mensajeOptimista = {
                contenido: contenido,
                remitente: { id: miIdDeUsuario }, // Simula la estructura de la BD
                timestamp: new Date().toISOString()
            };
            mostrarMensaje(mensajeOptimista);
            scrollAlFondo();

            // 4. Limpiar el input
            $chatInput.val('');
        }
    });


    // --- 6. ARRANQUE ---
    // Inicia todo el proceso.
    cargarHistorial();

});