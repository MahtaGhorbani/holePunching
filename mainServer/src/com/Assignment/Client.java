package com.Assignment;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private List<Address> addresses = new ArrayList<>();

    void putAddress(Address address) {
        this.addresses.add(address);
    }

    List<Address> getAddresses() {
        return addresses;
    }
}
class Address {
    private String address;
    private String port;

    Address(String address, String  port) {
        this.address = address;
        this.port = port;
    }

    String getAddress() {
        return address;
    }

    String getPort() {
        return port;
    }
}
