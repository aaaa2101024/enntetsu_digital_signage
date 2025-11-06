package com.example.enntetsu_digital_signage.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class Test {
    public HashMap<String, String> test() {
        HashMap<String, String> output = new HashMap<>();
        output.put("delay", "3");
        output.put("departure_time", "22:21");
        output.put("previous", "26");
        output.put("bus_number", "[40]聖隷三方原　尾張町　浜松駅");

        return output;
    }
}