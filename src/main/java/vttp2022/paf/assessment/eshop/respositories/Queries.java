package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    public static final String SQL_FIND_CUSTOMER_BY_NAME= "SELECT * FROM customers WHERE name =?";
    public static final String SQL_SAVE_ORDER ="INSERT INTO purchase_order(order_id, name, address, email) VALUES(?, ?, ?, ?)";
    public static final String SQL_SAVE_LINE_ITEMS = "INSERT INTO line_item(order_id, item, quantity) VALUES (?,?,?)";
}
