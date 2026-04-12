package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private Integer id;
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    // Detail fields (populated by movie detail API)
    private Integer runtime;
    private Long budget;

    @JsonProperty("origin_country")
    private List<String> originCountry;

    private List<Genre> genres;
}
