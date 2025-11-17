package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Mensaje;
import com.codeteam.rentaexpress.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    /**
     * Busca un historial de chat completo entre dos usuarios.
     * Trae los mensajes donde el remitente es A y el destinatario es B,
     * O donde el remitente es B y el destinatario es A,
     * y los ordena por fecha.
     */
    @Query("SELECT m FROM Mensaje m WHERE " +
            "(m.remitente = :usuarioA AND m.destinatario = :usuarioB) OR " +
            "(m.remitente = :usuarioB AND m.destinatario = :usuarioA) " +
            "ORDER BY m.timestamp ASC")
    List<Mensaje> findChatHistorial(
            @Param("usuarioA") Usuario usuarioA,
            @Param("usuarioB") Usuario usuarioB
    );

    /**
     * ¡LA "SUPER-CONSULTA" DE LA BANDEJA DE ENTRADA!
     * * Esto usa una función avanzada de Postgres (DISTINCT ON) para
     * encontrar el ÚLTIMO mensaje de cada conversación única en la que
     * un usuario ha participado.
     * * LEAST/GREATEST agrupan (usuario 1, usuario 36) y (usuario 36, usuario 1)
     * como la MISMA conversación.
     */
    @Query(value =
            "SELECT DISTINCT ON (LEAST(m.remitente_id, m.destinatario_id), GREATEST(m.remitente_id, m.destinatario_id)) * " +
                    "FROM mensajes m " +
                    "WHERE m.remitente_id = :miId OR m.destinatario_id = :miId " +
                    "ORDER BY LEAST(m.remitente_id, m.destinatario_id), GREATEST(m.remitente_id, m.destinatario_id), m.timestamp DESC",
            nativeQuery = true) // <-- ¡OJO! nativeQuery = true
    List<Mensaje> findConversacionesRecientes(@Param("miId") Integer miId);

}
