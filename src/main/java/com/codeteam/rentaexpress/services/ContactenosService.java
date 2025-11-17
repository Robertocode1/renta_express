package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Contactenos;
import com.codeteam.rentaexpress.repositories.ContactenosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContactenosService {

    private final ContactenosRepository contactenosRepo;
    private final EmailService emailService;


    @Transactional(readOnly = true)
    public List<Contactenos> listarContactos() {
        return contactenosRepo.findAll();
    }

    //Funcion para agregar mensajes
    public int agregarMensaje(Contactenos contacto) {
        Assert.notNull(contacto, "El usuario no puede ser nulo.");
        if(contacto.getCorreo().isEmpty()){
            return 2;
        }
        if(contacto.getAsunto().isEmpty() || contacto.getMensaje().isEmpty()){
            return 3;
        }
        Assert.notNull(contacto.getCorreo(), "El correo no puede ser nulo.");
        Assert.notNull(contacto.getAsunto(), "El asunto no puede ser nulo.");
        Assert.notNull(contacto.getMensaje(), "El mensaje no puede ser nulo.");

        try {
            contactenosRepo.save(contacto);
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }

    //Funcion para responder mensajes
    public int ResponderMensaje(String destinatario, String cuerpoMensaje, Integer mensajeId) {
        Assert.notNull(destinatario, "El usuario no puede ser nulo.");
        Assert.notNull(cuerpoMensaje, "El usuario no puede ser nulo.");
        try {
            emailService.sendEmail(destinatario, "Respuesta", cuerpoMensaje);
            contactenosRepo.deleteById(mensajeId);
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }



}
