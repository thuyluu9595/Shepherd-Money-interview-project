package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    // TODO: Credit card's owner. For detailed hint, please see User class
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    // TODO: Credit card's balance history. It is a requirement that the dates in the balanceHistory 
    //       list must be in chronological order, with the most recent date appearing first in the list. 
    //       Additionally, the first object in the list must have a date value that matches today's date, 
    //       since it represents the current balance of the credit card. For example:
    //       [
    //         {date: '2023-04-13', balance: 1500},
    //         {date: '2023-04-12', balance: 1200},
    //         {date: '2023-04-11', balance: 1000},
    //         {date: '2023-04-10', balance: 800}
    //       ]
    @OneToMany(cascade = CascadeType.ALL)
    private List<BalanceHistory> balanceHistory = new ArrayList<>();

    public CreditCard(String issuanceBank, String number, User owner) {
        this.issuanceBank = issuanceBank;
        this.number = number;
        this.owner = owner;

        BalanceHistory balanceHistory = new BalanceHistory();
        balanceHistory.setDate(Instant.now());
        balanceHistory.setBalance(0);
        this.balanceHistory.add(balanceHistory);
    }

    public void addBalanceHistory(BalanceHistory balanceHistory){
        this.balanceHistory.add(balanceHistory);
        // Sort the balance history list
        this.balanceHistory.sort(Comparator.comparing(BalanceHistory::getDate));
    }
}
