package ru.nsu.fit.pak.budle;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.nsu.fit.pak.budle.controller.EstablishmentController;
import ru.nsu.fit.pak.budle.controller.OrderController;
import ru.nsu.fit.pak.budle.dao.*;
import ru.nsu.fit.pak.budle.dto.request.RequestOrderDto;
import ru.nsu.fit.pak.budle.exceptions.EstablishmentNotFoundException;
import ru.nsu.fit.pak.budle.repository.OrderRepository;
import ru.nsu.fit.pak.budle.repository.UserRepository;
import ru.nsu.fit.pak.budle.service.OrderService;

import java.time.LocalDate;
import java.time.LocalTime;

class OrderBusinessLogicTests extends AbstractContextualTest {

    private static final Long GUEST_ID = 3L;

    private static final Long OWNER_ID = 200L;

    private static final Long ORDER_ID = 250L;

    private static final Long ESTABLISHMENT_ID = 100L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderController orderController;

    @Autowired
    private EstablishmentController establishmentController;

    @Test
    @DatabaseSetup(value = "/establishment/before/establishment.xml")
    public void testOrder_creatingOrder() {
        User guest = userRepository.findById(GUEST_ID).orElseThrow();
        mockUser(guest);
        long orderCount = orderRepository.findAll().size();
        RequestOrderDto order = new RequestOrderDto(
            4,
            LocalDate.now().plusDays(1),
            LocalTime.parse("14:30:00"),
            ESTABLISHMENT_ID,
            guest.getId(),
            null
        );
        orderController.create(order);
        orderRepository.findAll().get(0);
        Assertions.assertEquals(orderRepository.findAll().size(), orderCount + 1);
    }

    @Test
    @DatabaseSetup(value = "/establishment/before/establishment_with_order.xml")
    public void testOrder_acceptingAndRejectingOrder() {
        User owner = userRepository.findById(OWNER_ID).orElseThrow();
        mockUser(owner);
        establishmentController.accept(
            ESTABLISHMENT_ID,
            ORDER_ID,
            OrderStatus.REJECTED.getStatus()
        );
        Order order = orderRepository.findAll().get(0);
        Assertions.assertEquals(order.getStatus(), OrderStatus.REJECTED);

        establishmentController.accept(
            ESTABLISHMENT_ID,
            ORDER_ID,
            OrderStatus.ACCEPTED.getStatus()
        );
        order = orderRepository.findAll().get(0);
        Assertions.assertEquals(order.getStatus(), OrderStatus.ACCEPTED);
    }

    @Test
    public void tryToGetNonExistedOrder_mustBeThrownException() {
        Assertions.assertThrows(
            EstablishmentNotFoundException.class,
            () -> orderService.setStatus(111L, 22L, OrderStatus.ACCEPTED.getStatus())
        );
    }

    private void mockUser(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
    }

}
