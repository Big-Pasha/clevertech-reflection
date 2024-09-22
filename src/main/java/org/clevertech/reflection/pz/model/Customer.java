package org.clevertech.reflection.pz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Order> orders;
}
