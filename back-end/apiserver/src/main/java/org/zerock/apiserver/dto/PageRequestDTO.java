package org.zerock.apiserver.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;
}
