package com.example.chatrepo.dto;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Attachment {
    private String type;
    private String url;
}
