package com.usktea.plainold.applications.order;

import com.usktea.plainold.applications.user.GetUserService;
import com.usktea.plainold.dtos.OrderDetailDto;
import com.usktea.plainold.exceptions.OrderNotFound;
import com.usktea.plainold.models.order.Order;
import com.usktea.plainold.models.order.OrderNumber;
import com.usktea.plainold.models.user.Username;
import com.usktea.plainold.models.user.Users;
import com.usktea.plainold.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GetOrderDetailService {
    private final GetUserService getUserService;
    private final GetOrderService getOrderService;

    public GetOrderDetailService(GetUserService getUserService, GetOrderService getOrderService) {
        this.getUserService = getUserService;
        this.getOrderService = getOrderService;
    }

    public OrderDetailDto getOrder(Username username, OrderNumber orderNumber) {
        Users user = getUserService.find(username);
        Order order = getOrderService.find(orderNumber);

        order.authenticate(user.username());

        return order.toDetailDto();
    }
}
