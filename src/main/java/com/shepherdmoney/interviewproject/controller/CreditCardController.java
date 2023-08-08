package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    // TODO: wire in CreditCard repository here (~1 line)
    @Autowired
    private final CreditCardRepository creditCardRepository;

    public CreditCardController(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length

        Optional<User> user = creditCardRepository.findById(payload.getUserId());

        // Check if the user exists
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Create credit card from payload
        CreditCard newCreditCard = new CreditCard(payload.getCardIssuanceBank(),payload.getCardNumber(),user.get());

        // Save credit card
        creditCardRepository.save(newCreditCard);

        return ResponseEntity.ok(newCreditCard.getId());
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null

        // Find all credit cards by user id
        List<CreditCard> creditCards = creditCardRepository.findAllByOwnerId(userId);

        // Map CreditCard objects to CreditCardView object and return as list
        List<CreditCardView> creditCardViews = creditCards.stream()
                .map(creditCard -> new CreditCardView(creditCard.getIssuanceBank(), creditCard.getNumber()))
                .toList();

        return ResponseEntity.ok(creditCardViews);


    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request

        // Find user by credit card number
        Optional<User> user = creditCardRepository.findUserByNumber(creditCardNumber);

        // Check if the user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(user.get().getId());
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<Void> postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 110}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        for (UpdateBalancePayload updateBalance : payload) {
            CreditCard creditCard = creditCardRepository.findByNumber(updateBalance.getCreditCardNumber());
            if (creditCard == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Update the balance history
            BalanceHistory balanceHistory = new BalanceHistory();
            balanceHistory.setDate(updateBalance.getTransactionTime());
            balanceHistory.setBalance(updateBalance.getTransactionAmount());

            creditCard.addBalanceHistory(balanceHistory);

            creditCardRepository.save(creditCard);
        }

        return ResponseEntity.ok().build();

    }
    
}
