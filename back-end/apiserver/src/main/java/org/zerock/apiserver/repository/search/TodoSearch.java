package org.zerock.apiserver.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.zerock.apiserver.domain.Todo;
import org.zerock.apiserver.dto.PageRequestDTO;
import org.zerock.apiserver.dto.PageResponseDTO;

public interface TodoSearch {
    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
