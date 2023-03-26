package ru.nsu.fit.pak.budle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.nsu.fit.pak.budle.dto.OrderDto;
import ru.nsu.fit.pak.budle.dto.OrderDtoOutput;
import ru.nsu.fit.pak.budle.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "order", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public void create(@RequestBody @Valid OrderDto dto) {
        orderService.createOrder(dto);

    }

    @DeleteMapping
    public void delete(@RequestParam Long orderId,
                       @RequestParam Long userId) {
        orderService.deleteOrder(orderId, userId, Boolean.TRUE);
    }

    @GetMapping
    public List<OrderDtoOutput> get(@RequestParam Long userId,
                                    @RequestParam(required = false) Integer status) {

        return orderService.getOrders(userId, Boolean.TRUE, status);
    }


}
