package org.zerock.mallapi.service;

import java.util.List;
import org.zerock.mallapi.dto.CartItemDTO;
import org.zerock.mallapi.dto.CartItemListDTO;
import jakarta.transaction.Transactional;
@Transactional
public interface CartService {
    //장바구니 아이템 추가 혹은 변경
    //모든 장바구니 아이템 목록
    //아이템 삭제

    List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO);

    List<CartItemListDTO> getCartItems(String email);

    List<CartItemListDTO> remove(Long cino);
}