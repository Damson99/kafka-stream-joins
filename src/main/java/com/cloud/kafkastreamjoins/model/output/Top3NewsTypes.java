package com.cloud.kafkastreamjoins.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.TreeSet;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"top3Sorted"})
public class Top3NewsTypes {

    private final ObjectMapper objectMapper= new ObjectMapper();

    private final TreeSet<ClicksByNewsType> top3Sorted= new TreeSet<>((o1, o2) -> {
//        sorting by clicks than by newsType
        final int result= o2.getClicks().compareTo(o1.getClicks());
        if(result!=0)
            return result;
        else
            return o1.getNewsType().compareTo(o2.getNewsType());
    });


    public void add(ClicksByNewsType newClick) {

        top3Sorted.add(newClick);
        if(top3Sorted.size()>3)
            top3Sorted.remove(top3Sorted.last());
    }

    public void remove(ClicksByNewsType oldClick) {

        top3Sorted.remove(oldClick);
    }

    @JsonProperty("top3Sorted")
    public String getTop3Sorted() throws JsonProcessingException {

        return objectMapper.writeValueAsString(top3Sorted);
    }

    @JsonProperty("top3Sorted")
    public void set3TopSorted(String top3String) throws IOException {

        ClicksByNewsType[] top3News= objectMapper.readValue(top3String, ClicksByNewsType[].class);
        for(ClicksByNewsType click : top3News) {
            add(click);
        }
    }
}
