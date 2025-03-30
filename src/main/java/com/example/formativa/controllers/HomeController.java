package com.example.formativa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.formativa.backend.TokenStore;
import com.example.formativa.models.Event;
import com.example.formativa.repositories.EventRepository;

@Controller
public class HomeController {

    @Autowired
    private EventRepository eventRepository;

    private final TokenStore tokenStore;

    @Autowired
    public HomeController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Event> latestEvents = eventRepository.findTop10ByOrderByIdDesc();
        List<Event> featuredEvents = eventRepository.findTop10ByOrderByIdAsc();

        model.addAttribute("latestEvents", latestEvents);
        model.addAttribute("featuredEvents", featuredEvents);
        return "home";
    }

    @GetMapping("/greetings")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="Juan González") String name, Model model) {
        System.out.println("Name: " + name);
        String url = "http://localhost:8080/greetings";
        RestTemplate restTemplate = new RestTemplate();

        String token = tokenStore.getToken();
        System.out.println("Token: " + token);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("name", name);


        ResponseEntity<String> response = restTemplate.exchange(
            builder.toUriString(), HttpMethod.GET, entity, String.class);

        System.out.println("Response: " + response);

        model.addAttribute("name", response.getBody());
        return "Greetings";
    }
}
