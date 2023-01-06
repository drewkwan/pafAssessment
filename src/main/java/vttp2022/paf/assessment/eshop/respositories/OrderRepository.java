package vttp2022.paf.assessment.eshop.respositories;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.services.OrderException;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

@Repository
public class OrderRepository {
	// TODO: Task 3
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional(rollbackFor = OrderException.class)
	public int saveOrder(Order order) throws OrderException {

		//generate the orderId
		String orderId = UUID.randomUUID().toString().substring(0, 8);
		order.setOrderId(orderId);
		System.out.printf("orderId = %s\n", orderId);

		//create the order
		int count = jdbcTemplate.update(SQL_SAVE_ORDER, 
								order.getOrderId(), 
								order.getName(),
								order.getAddress(),
								order.getEmail());

		if (count == 0) {
			throw new OrderException("Exception for orderId %s\n".formatted(orderId));
		}

		//create the associated items
		addLineItems(order.getLineItems(), orderId);

		return count;

	}

	public void addLineItems (List<LineItem> lineItems, String orderId) {

		List<Object[]> data = lineItems.stream()
							.map(li -> {
								Object[] l = new Object[3];
								l[0] = orderId;
								l[1] = li.getItem();
								l[2] = li.getQuantity();
								return l;
							})
							.toList();

		jdbcTemplate.batchUpdate(SQL_SAVE_LINE_ITEMS, data);
	}

}
