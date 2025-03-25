package com.email.writer.Controller;

import com.email.writer.DTO.EmailRequest;
import com.email.writer.Service.EmailGeneratorService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGenerator {
    private final EmailGeneratorService emailGeneratorService;
    public EmailGenerator(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response=emailGeneratorService.generateEmailReply(emailRequest);
         return ResponseEntity.ok(response);
    }
}
