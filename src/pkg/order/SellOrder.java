package pkg.order;

import pkg.exception.StockMarketExpection;
import pkg.trader.Trader;

public class SellOrder extends Order {
	public SellOrder(String stockSymbol, int size, double price, Trader trader) {
		this.orderNumber = getNextOrderNumber();
		setStockSymbol(stockSymbol);
		setSize(size);
		setPrice(price);
		setTrader(trader);
	}

	public SellOrder(String stockSymbol, int size, boolean isMarketOrder,
			Trader trader) throws StockMarketExpection {
		this.orderNumber = getNextOrderNumber();
		setStockSymbol(stockSymbol);
		setSize(size);
		if (isMarketOrder == true) {
			setMarketOrder(isMarketOrder);
			setPrice((float) 0.00);
		} else {
			throw new StockMarketExpection("Sell order for stock "
					+ stockSymbol + " placed without a valid price.");
		}
		setTrader(trader);
	}

	

	public void printOrder() {
		System.out.println("Stock: " + stock + " $" + d + " x "
				+ size + " (Sell)");
	}
}
