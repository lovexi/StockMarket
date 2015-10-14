package pkg.order;

import pkg.trader.Trader;

public abstract class Order {
	int size;
	double d;
	boolean isMarketOrder = false;
	Trader trader;
	int orderNumber;
	String stock;

	/** LOCK */
	public static final Object LOCK = new Object();

	/** Last Order Number */
	private static int lastOrderNumber = -1;

	/** */
	protected static int getNextOrderNumber() {
		synchronized (LOCK) {
			lastOrderNumber++;
			return lastOrderNumber;
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getPrice() {
		return d;
	}

	public void setPrice(double price) {
		this.d = price;
	}

	public boolean isMarketOrder() {
		return isMarketOrder;
	}

	public void setMarketOrder(boolean isMarketOrder) {
		this.isMarketOrder = isMarketOrder;
	}

	public Trader getTrader() {
		return trader;
	}

	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getStockSymbol() {
		return stock;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stock = stockSymbol;
	}

	/** */
	public boolean equals(Object o) {
		return ((Order) o).getOrderNumber() == this.getOrderNumber();
	}

	public void printStockNameInOrder() {
		System.out.println(stock);
	}

	public abstract void printOrder();

}
