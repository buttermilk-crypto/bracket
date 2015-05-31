package asia.redact.bracket.exampleclasses;

import java.util.List;

public class PurchaseOrder extends ExampleBase  {
	
	String orderDate;
	
	ShipTo shipTo;
	BillTo billTo;
	
	List<Item> items;

	public PurchaseOrder() {}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public ShipTo getShipTo() {
		return shipTo;
	}

	public void setShipTo(ShipTo shipTo) {
		this.shipTo = shipTo;
	}

	public BillTo getBillTo() {
		return billTo;
	}

	public void setBillTo(BillTo billTo) {
		this.billTo = billTo;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
