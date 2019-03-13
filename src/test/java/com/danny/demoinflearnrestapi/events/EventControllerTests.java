package com.danny.demoinflearnrestapi.events;

import com.danny.demoinflearnrestapi.common.RestDocsConfiguration;
import com.danny.demoinflearnrestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2019, 3, 4, 12, 0, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2019, 3, 5, 12, 0, 0))
            .beginEventDateTime(LocalDateTime.of(2019, 3, 6, 12, 0, 0))
            .endEventDateTime(LocalDateTime.of(2019, 3, 7, 12, 0, 0))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역")
            .build();

        mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaTypes.HAL_JSON)
            .content(objectMapper.writeValueAsString(event))
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
            .andDo(document("create-event",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("query-events").description("link to query events"),
                    linkWithRel("update-event").description("link to update an existing event"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("name").description("Name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                    fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                    fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                    fieldWithPath("endEventDateTime").description("date time of end of new event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                    headerWithName(HttpHeaders.LOCATION).description("location header")
                ),
                responseFields(
                    fieldWithPath("id").description("identifier of new event"),
                    fieldWithPath("name").description("Name of new event"),
                    fieldWithPath("description").description("description of new event"),
                    fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                    fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                    fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                    fieldWithPath("endEventDateTime").description("date time of end of new event"),
                    fieldWithPath("location").description("location of new event"),
                    fieldWithPath("basePrice").description("base price of new event"),
                    fieldWithPath("maxPrice").description("max price of new event"),
                    fieldWithPath("limitOfEnrollment").description("limit of enrollment of new event"),
                    fieldWithPath("free").description("it tells is this event is free or not"),
                    fieldWithPath("offline").description("it tells is this event is offline event or not"),
                    fieldWithPath("eventStatus").description("event status"),
                    fieldWithPath("_links.self.href").description("link to self"),
                    fieldWithPath("_links.query-events.href").description("link to query event list"),
                    fieldWithPath("_links.update-event.href").description("link to update existing event"),
                    fieldWithPath("_links.profile.href").description("link to profile")
                )
            ));
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
            .id(100)
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2019, 3, 4, 12, 0, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2019, 3, 5, 12, 0, 0))
            .beginEventDateTime(LocalDateTime.of(2019, 3, 6, 12, 0, 0))
            .endEventDateTime(LocalDateTime.of(2019, 3, 7, 12, 0, 0))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역")
            .free(true)
            .offline(false)
            .build();

        mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaTypes.HAL_JSON)
            .content(objectMapper.writeValueAsString(event))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void creatEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void creatEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2019, 3, 4, 12, 0, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2019, 3, 5, 12, 0, 0))
            .beginEventDateTime(LocalDateTime.of(2019, 3, 8, 12, 0, 0))
            .endEventDateTime(LocalDateTime.of(2019, 3, 7, 12, 0, 0))
            .basePrice(10000)
            .maxPrice(200)
            .location("강남역")
            .limitOfEnrollment(100)
            .build();

        this.mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("content[0].objectName").exists())
            .andExpect(jsonPath("content[0].defaultMessage").exists())
            .andExpect(jsonPath("content[0].code").exists())
            .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지를 조회하기")
    public void queryEvents() throws Exception {
        // 이벤트 30개 저장
        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(get("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaTypes.HAL_JSON)
            .param("page", "1")
            .param("size", "10")
            .param("sort", "name,DESC")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andDo(document("get-events",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("first").description("link to first page"),
                    linkWithRel("prev").description("link to prev page"),
                    linkWithRel("next").description("link to next page"),
                    linkWithRel("last").description("link to last page"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestParameters(
                    parameterWithName("page").description("요청페이지 번호"),
                    parameterWithName("size").description("페이지 사이즈"),
                    parameterWithName("sort").description("정렬")
                )
            ))
        ;
    }

    private void generateEvent(int index) {
        Event event = Event.builder()
            .name("event" + index)
            .description("test event")
            .build();

        this.eventRepository.save(event);
    }
}
