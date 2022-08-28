package com.pipikonda.translationbot.dto;

import com.pipikonda.translationbot.domain.Lang;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class WordCreateDto {

    @NotNull
    @Size(min = 2)
    Map<Lang, List<String>> translations;

    @NotEmpty
    String userId;

}
