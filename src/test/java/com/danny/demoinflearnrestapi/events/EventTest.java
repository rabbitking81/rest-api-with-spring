package com.danny.demoinflearnrestapi.events;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EventTest {
    @Test
    public void builder() {
        Event event = Event.builder()
            .name("Inflearn Spring REST API")
            .description("REST API development with Spring")
            .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        String name = "Event";
        String description = "Spring";

        Event event = new Event();
        event.setName("Event");
        event.setDescription("Spring");

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() {
        Event event = Event.builder()
            .basePrice(0)
            .maxPrice(0)
            .build();

        event.update();
        assertThat(event.isFree()).isTrue();

        event = Event.builder()
            .basePrice(100)
            .maxPrice(0)
            .build();

        event.update();
        assertThat(event.isFree()).isFalse();

        event = Event.builder()
            .basePrice(0)
            .maxPrice(100)
            .build();

        event.update();
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        Event event = Event.builder()
            .location("강남역")
            .build();

        event.update();

        assertThat(event.isOffline()).isTrue();

        event = Event.builder()
            .build();

        event.update();

        assertThat(event.isOffline()).isFalse();
    }
}