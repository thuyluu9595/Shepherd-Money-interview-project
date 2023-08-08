package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "MyUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String email;

    // TODO: User's credit card
    // HINT: A user can have one or more, or none at all. We want to be able to query credit cards by user
    //       and user by a credit card.
    @OneToMany(mappedBy = "owner")
    private List<CreditCard> creditCards = new ArrayList<>();
    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public void addCreditCard(CreditCard creditCard){
        this.creditCards.add(creditCard);
        creditCard.setOwner(this);
    }

    public void removeCreditCard(CreditCard creditCard){
        this.creditCards.remove(creditCard);
        creditCard.setOwner(null);
    }
}
