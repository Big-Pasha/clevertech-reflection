package org.clevertech.reflection.pz;

import org.clevertech.reflection.pz.model.Customer;
import org.clevertech.reflection.pz.parser.ParseJ;
import org.clevertech.reflection.pz.util.ModelSupplier;

public class Main {
    public static void main(String[] args) throws Exception {
        // криво но работает :)
        ParseJ parseJ = new ParseJ();

        Customer customer = ModelSupplier.getCustomer();
        String customerJson = parseJ.parseToJsonInLine(customer);
        Customer parsedCustomer = parseJ.parseFromJson(Customer.class, customerJson);

        System.out.println(customer.equals(parsedCustomer));
    }
}