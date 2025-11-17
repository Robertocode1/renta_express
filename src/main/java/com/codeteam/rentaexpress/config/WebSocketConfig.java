package com.codeteam.rentaexpress.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // ¡Activa el "cerebro" del chat!
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 1. EL OPERADOR TELEFÓNICO (Broker)
        // Estos son los prefijos que el SERVIDOR usa para enviar mensajes
        // de vuelta al CLIENTE.
        // /queue -> Para mensajes privados 1-a-1
        // /topic -> Para mensajes públicos/grupales (broadcast)
        config.enableSimpleBroker("/queue", "/topic");

        // 2. EL DESTINO DE LA APP
        // Este es el prefijo que el CLIENTE usa para enviar mensajes
        // al SERVIDOR.
        // (Ej: El JS enviará a "/app/chat.privado")
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 3. EL PUNTO DE CONEXIÓN (Endpoint)
        // Esta es la URL HTTP que el JavaScript usará para
        // conectarse por primera vez al WebSocket.
        // (Ej: new SockJS('/ws-chat'))
        registry.addEndpoint("/ws-chat")
                .withSockJS(); // .withSockJS() es un 'plan B' por si el
        // navegador del cliente es muy viejo y no
        // soporta WebSockets nativos.
    }
}