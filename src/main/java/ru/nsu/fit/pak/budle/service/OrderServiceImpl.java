package ru.nsu.fit.pak.budle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.nsu.fit.pak.budle.dao.Order;
import ru.nsu.fit.pak.budle.dao.OrderStatus;
import ru.nsu.fit.pak.budle.dao.User;
import ru.nsu.fit.pak.budle.dao.establishment.Establishment;
import ru.nsu.fit.pak.budle.dto.ValidTimeDto;
import ru.nsu.fit.pak.budle.dto.request.RequestOrderDto;
import ru.nsu.fit.pak.budle.dto.response.ResponseOrderDto;
import ru.nsu.fit.pak.budle.exceptions.EstablishmentNotFoundException;
import ru.nsu.fit.pak.budle.exceptions.InvalidBookingTime;
import ru.nsu.fit.pak.budle.exceptions.NotEnoughRightsException;
import ru.nsu.fit.pak.budle.exceptions.OrderNotFoundException;
import ru.nsu.fit.pak.budle.mapper.EstablishmentMapper;
import ru.nsu.fit.pak.budle.mapper.OrderMapper;
import ru.nsu.fit.pak.budle.mapper.WorkingHoursMapper;
import ru.nsu.fit.pak.budle.repository.EstablishmentRepository;
import ru.nsu.fit.pak.budle.repository.OrderRepository;
import ru.nsu.fit.pak.budle.utils.OrderFactory;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final SecurityService securityService;

    private final EstablishmentService establishmentService;

    private final EstablishmentMapper establishmentMapper;

    private final EstablishmentRepository establishmentRepository;


    private final OrderFactory orderFactory;

    private final WorkingHoursService workingHoursService;

    private final WorkingHoursMapper workingHoursMapper;

    private final OrderMapper orderMapper;


    @Override
    public void createOrder(RequestOrderDto dto) {
        log.info("Creating order");
        log.info(dto.toString());
        Establishment establishment = establishmentService
                .getEstablishmentById(dto.getEstablishmentId());
        if (!bookingTimeIsValid(establishment, dto)) {
            log.warn("Booking time is not valid");
            throw new InvalidBookingTime();
        }

        Class<? extends Order> mappingClass = orderFactory.getEntity(dto);
        Order order = orderMapper.toEntity(dto, mappingClass);

        User user = securityService.getLoggedInUser();
        order.setUser(user);
        order.setEstablishment(establishment);
        orderRepository.save(order);
    }

    private boolean bookingTimeIsValid(Establishment establishment, RequestOrderDto order) {
        List<ValidTimeDto> validTimeDtos =
                workingHoursService.getValidBookingHoursByEstablishment(establishment);
        ValidTimeDto orderTime = workingHoursMapper.convertFromDateAndTimeToValidTimeDto(order.getDate());
        String bookingTime = order.getTime().toString();
        for (ValidTimeDto time : validTimeDtos) {
            if (Objects.equals(time.getDayName(), orderTime.getDayName()) &&
                    Objects.equals(time.getMonthName(), orderTime.getMonthName()) &&
                    Objects.equals(time.getDayNumber(), orderTime.getDayNumber()) &&
                    time.getTimes().contains(bookingTime)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<ResponseOrderDto> getOrders(Long userId, Long establishmentId, Integer status) {
        log.info("Getting orders");
        log.info("id " + userId);
        log.info("establishment " + establishmentId);

        User user = securityService.getLoggedInUser();

        Establishment establishment = establishmentId == null ?
                null : establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new EstablishmentNotFoundException(establishmentId));

        OrderStatus orderStatus = status == null ? null : OrderStatus.getStatusByInteger(status);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Order> exampleQuery = Example.of(new Order(user, establishment, orderStatus), matcher);
        List<Order> orders = orderRepository.findAll(exampleQuery);

        log.info("Result: " + orders);

        return orders
                .stream()
                .map(order -> {
                    Establishment establishmentSource = order.getEstablishment();
                    ResponseOrderDto responseOrderDto = modelMapper.map(order, ResponseOrderDto.class);
                    responseOrderDto.setEstablishment(establishmentMapper.toBasic(establishmentSource));
                    responseOrderDto.setUserId(order.getUser().getId());
                    return responseOrderDto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        log.info("Deleting order");
        log.info("OrderID " + orderId + "\n" + "id " + userId);

        Order order = getOrderById(orderId);

        if (order.getUser().getId().equals(userId)) {
            orderRepository.delete(order);
        } else {
            log.warn("Not enough right for this operation");
            throw new NotEnoughRightsException();
        }
    }

    @Override
    @Transactional
    public void setStatus(Long orderId, Long establishmentId, Integer status) {
        log.info("Setting order status");
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.getStatusByInteger(status));
        orderRepository.save(order);
    }


    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() ->
                new OrderNotFoundException(orderId));
    }

}
