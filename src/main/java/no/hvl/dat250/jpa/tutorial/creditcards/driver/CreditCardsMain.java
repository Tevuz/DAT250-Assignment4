package no.hvl.dat250.jpa.tutorial.creditcards.driver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import no.hvl.dat250.jpa.tutorial.creditcards.*;

public class CreditCardsMain {

  static final String PERSISTENCE_UNIT_NAME = "jpa-tutorial";

  public static void main(String[] args) {
    try (EntityManagerFactory factory = Persistence.createEntityManagerFactory(
        PERSISTENCE_UNIT_NAME); EntityManager em = factory.createEntityManager()) {
      em.getTransaction().begin();
      createObjects(em);
      em.getTransaction().commit();
    }

  }

  private static void createObjects(EntityManager em) {
    // Create bank
    Bank bank = new Bank();
    bank.setName("Pengebank");
    em.persist(bank);

    // Create customer
    Customer customer = new Customer();
    customer.setName("Max Mustermann");
    em.persist(customer);

    // Create address
    Address address = new Address();
    address.setStreet("Inndalsveien");
    address.setNumber(28);
    em.persist(address);

    address.getOwners().add(customer);
    customer.getAddresses().add(address);
    em.persist(address);
    em.persist(customer);

    // Create Pincode
    Pincode pincode = new Pincode();
    pincode.setCode("123");
    pincode.setCount(1);
    em.persist(pincode);

    // Create card 1
    CreditCard card1 = new CreditCard();
    card1.setNumber(12345);
    card1.setBalance(-5000);
    card1.setCreditLimit(-10000);
    em.persist(card1);

    card1.setPincode(pincode);
    pincode.getCards().add(card1);
    em.persist(pincode);
    em.persist(card1);

    card1.getOwners().add(customer);
    customer.getCreditCards().add(card1);
    em.persist(customer);
    em.persist(card1);

    card1.setOwningBank(bank);
    bank.getOwnedCards().add(card1);
    em.persist(bank);
    em.persist(card1);

    // Create card 2
    CreditCard card2 = new CreditCard();
    card2.setNumber(123);
    card2.setBalance(1);
    card2.setCreditLimit(2000);
    em.persist(card2);

    card2.setPincode(pincode);
    pincode.getCards().add(card2);
    em.persist(pincode);
    em.persist(card2);

    card2.getOwners().add(customer);
    customer.getCreditCards().add(card2);
    em.persist(customer);
    em.persist(card2);

    card2.setOwningBank(bank);
    bank.getOwnedCards().add(card2);
    em.persist(bank);
    em.persist(card2);
  }
}
