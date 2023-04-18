package ru.nsu.fit.pak.budle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.nsu.fit.pak.budle.controller.EstablishmentController;
import ru.nsu.fit.pak.budle.controller.OrderController;
import ru.nsu.fit.pak.budle.dao.Category;
import ru.nsu.fit.pak.budle.dao.Order;
import ru.nsu.fit.pak.budle.dao.User;
import ru.nsu.fit.pak.budle.dao.establishment.Establishment;
import ru.nsu.fit.pak.budle.dto.OrderDto;
import ru.nsu.fit.pak.budle.dto.OrderDtoOutput;
import ru.nsu.fit.pak.budle.repository.EstablishmentRepository;
import ru.nsu.fit.pak.budle.repository.OrderRepository;
import ru.nsu.fit.pak.budle.repository.UserRepository;
import ru.nsu.fit.pak.budle.service.EstablishmentService;
import ru.nsu.fit.pak.budle.service.OrderService;

import javax.transaction.Transactional;
import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@SpringBootTest(classes = BudleApplication.class)
@Testcontainers
class OrderBusinessLogicTests {

    @Autowired
    private EstablishmentService establishmentService;
    @Autowired
    private EstablishmentRepository establishmentRepository;
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

    private Establishment mainEstablishment;

    private User guest;


    @Test
    @Transactional
    public void testOrder_creatingRejectingAcceptingAndDeletingSequential() {
        insertEstablishments();
        long orderCount = orderRepository.findAll().size();
        OrderDto order = new OrderDto(4, new Date(), new Time(14, 30, 0),
                mainEstablishment.getId(), guest.getId(), null);
        orderController.create(order);
        Order createdOrder = orderRepository.findAll().get(0);
        System.out.println(createdOrder);
        Assertions.assertEquals(orderRepository.findAll().size(), orderCount + 1);
        establishmentController.deleteOrder(createdOrder.getId(), mainEstablishment.getId());
        Assertions.assertEquals(createdOrder.getStatus(), 2);
        establishmentController.accept(mainEstablishment.getId(), createdOrder.getId());
        Assertions.assertEquals(createdOrder.getStatus(), 1);

        List<OrderDtoOutput> listFromUser = orderService.getOrders(guest.getId(), true, 1);
        List<OrderDtoOutput> listFromEstablishment = orderService.getOrders(mainEstablishment.getId(), false, 1);
        Assertions.assertEquals(listFromEstablishment, listFromUser);


        orderController.delete(createdOrder.getId(), guest.getId());
        Assertions.assertEquals(orderRepository.findAll().size(), orderCount);

    }

    @Transactional
    public void insertEstablishments() {
        User ownerOfAllEstablishments = new User(0L, "Oleg", "+79993332211", "123456");
        userRepository.saveAndFlush(ownerOfAllEstablishments);
        guest = new User(3L, "Varya", "+1111111111", "123456");
        guest = userRepository.saveAndFlush(guest);
        User user = userRepository.findAll().get(0);
        mainEstablishment = new Establishment(1L,
                "Red Rabbit",
                "Small bar",
                "Koshurnikova st, 47",
                false,
                false,
                4.9F,
                400,
                Category.barbershop,
                "Some image",
                "Some map",
                user,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet());
        mainEstablishment = establishmentRepository.saveAndFlush(mainEstablishment);
    }
}
