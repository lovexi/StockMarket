
package pkg.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import pkg.client.Pair;
import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.PriceSetter;
import pkg.util.OrderUtility;

public class OrderBook {
	Market m;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
	}
	
	// (All Correct) This part is to add stock into orderBook. (For both buy order and sell order)
	public void addToOrderBook(Order order) {
		String stockSymbol = order.getStockSymbol();
		if (order instanceof BuyOrder) {
			ArrayList<Order> buyOrderList = null;
			if (buyOrders.containsKey(stockSymbol)) {
				buyOrderList = buyOrders.remove(stockSymbol);
			} else {
				buyOrderList = new ArrayList<Order>();
			}
			buyOrderList.add(order);
			buyOrders.put(stockSymbol, buyOrderList);
		} else if (order instanceof SellOrder) {
			ArrayList<Order> sellOrderList = null;
			if (sellOrders.containsKey(stockSymbol)) {
				sellOrderList = sellOrders.remove(stockSymbol);
			} else {
				sellOrderList = new ArrayList<Order>();
			}
			sellOrderList.add(order);
			sellOrders.put(stockSymbol, sellOrderList);
		}
	}

	public void trade() {
		Set<String> stocksToBuy = buyOrders.keySet();

		for (String stockToBuy : stocksToBuy) {
			// If the stock is not existing in the sellorder, skip this stock.
			if (!sellOrders.containsKey(stockToBuy)) {
				continue;
			}
			
			// Remove the specific stock orders directly and store them in arraylists.
			ArrayList<Order> buyOrderList = buyOrders.remove(stockToBuy);
			ArrayList<Order> sellOrderList = sellOrders.remove(stockToBuy);
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
			calculateCumulative(buyOrderList, sellOrderList, orderCumulative);
			double matchPrice = findMatchPrice(orderCumulative);
			// Perform trade
			ArrayList<Order> CopyOfBuyOrder = (ArrayList<Order>) buyOrderList.clone();
			ArrayList<Order> CopyOfSellOrder = (ArrayList<Order>) sellOrderList.clone();
			if (matchPrice > 0.0) {
				setPrice(stockToBuy, matchPrice);
			}
			
			Pair<Integer, Integer> matchPair = orderCumulative.get(matchPrice);
			int buySize = matchPair.getLeft();
			int sellSize = matchPair.getRight();
			int restSize = 0;
			boolean buyLeft = false;
			boolean sellLeft = false;
			if (buySize > sellSize){
				buyLeft = true;
				restSize = buySize - sellSize;
			}else if (sellSize > buySize){
				sellLeft = true;
				restSize = sellSize - buySize;
			}
			
			doBuyOrder(stockToBuy, buyOrderList, matchPrice, CopyOfBuyOrder, restSize,
					buyLeft);
			
			doSellOrder(stockToBuy, sellOrderList, matchPrice, CopyOfSellOrder,
					restSize, sellLeft);
		}
	}

	private void doSellOrder(String stockToBuy, ArrayList<Order> sellO,
			double matchPrice, ArrayList<Order> CopyOfSellOrder, int restSize,
			boolean sellLeft) {
		for (Order sellOrder : CopyOfSellOrder) {
			if (sellOrder.getPrice() == matchPrice){
				if (sellLeft){
					sellOrder.setSize(sellOrder.getSize()-restSize);
					tradeSellOrder(matchPrice, sellOrder);
					sellOrder.setSize(restSize);
				}
				else{
					sellO.remove(sellOrder);
					tradeSellOrder(matchPrice, sellOrder);
				}
			}
			if (sellOrder.getPrice() < matchPrice
					|| sellOrder.isMarketOrder()) {
				sellO.remove(sellOrder);
				tradeSellOrder(matchPrice, sellOrder);
			}
		}
		sellOrders.put(stockToBuy, sellO);
	}

	private void tradeSellOrder(double matchPrice, Order sellOrder) {
		try {
			sellOrder.trader.tradePerformed(sellOrder,
					matchPrice);
		} catch (StockMarketExpection e) {
			e.printStackTrace();
		}
	}

	private void doBuyOrder(String stockToBuy, ArrayList<Order> buyO,
			double matchPrice, ArrayList<Order> CopyOfBuyOrder, int restSize,
			boolean buyLeft) {
		for (Order buyOrder : CopyOfBuyOrder) {
			if (buyOrder.getPrice() == matchPrice){
				if (buyLeft){
					buyOrder.setSize(buyOrder.getSize()-restSize);
					performBuyOrder(matchPrice, buyOrder);
					buyOrder.setSize(restSize);
				}
				else{
					buyO.remove(buyOrder);
					performBuyOrder(matchPrice, buyOrder);
				}
			}
			if (buyOrder.getPrice() > matchPrice
					|| buyOrder.isMarketOrder()) {
				buyO.remove(buyOrder);
				performBuyOrder(matchPrice, buyOrder);
			}
		}
		buyOrders.put(stockToBuy, buyO);
	}

	private void performBuyOrder(double matchPrice, Order buyOrder) {
		try {
			buyOrder.getTrader().tradePerformed(buyOrder,
					matchPrice);
		} catch (StockMarketExpection e) {
			e.printStackTrace();
		}
	}

	private void setPrice(String stockToBuy, double matchPrice) {
		PriceSetter ps = new PriceSetter();
		ps.registerObserver(m.getMarketHistory());
		m.getMarketHistory().setSubject(ps);
		ps.setNewPrice(m, stockToBuy, matchPrice);
	}

	private double findMatchPrice(
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative) {
		double matchPrice = initializeForMatchPrice();
		int matchQuantity = 0;
		for (double orderPrice : orderCumulative.keySet()) {
			Pair<Integer, Integer> orderPair = orderCumulative.get(orderPrice);
			if (orderPair.getLeft() <= orderPair.getRight()) {
				if (orderPair.getLeft() > matchQuantity) {
					matchPrice = orderPrice;
					matchQuantity = orderPair.getLeft();
				}
			} else if (orderPair.getLeft() > orderPair.getRight()) {
				if (orderPair.getRight() > matchQuantity) {
					matchPrice = orderPrice;
					matchQuantity = orderPair.getRight();
				}
			}
		}
		return matchPrice;
	}

	private double initializeForMatchPrice() {
		double matchPrice = (float) 0.0;
		return matchPrice;
	}

	private void calculateCumulative(ArrayList<Order> buyOrderList,
			ArrayList<Order> sellOrderList,
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative) {
		// Culculate the buy cumulative orders.
		enterBuyCumulativeOrders(buyOrderList, orderCumulative);

		int totalSells = 0;
		for (Order sellOrder : sellOrderList) {
			double sellPrice = sellOrder.getPrice();
			// If it is a sell market order, put it in the end
			if (orderCumulative.containsKey(sellPrice)) {
				Pair<Integer, Integer> cumulativePair = orderCumulative
						.remove(sellPrice);
				orderCumulative.put(sellPrice, new Pair<Integer, Integer>(
						cumulativePair.getLeft(), sellOrder.getSize() + cumulativePair.getRight()));
			} else {
				if (sellOrder.isMarketOrder() || sellOrder.getPrice() == 0.0) {
					orderCumulative.put(sellPrice, new Pair<Integer, Integer>(
							0, sellOrder.getSize()));
				} else {
					Double nearestPrice = orderCumulative
							.ceilingKey(sellPrice);
					addToNearestPrice(orderCumulative, sellOrder, sellPrice,
							nearestPrice);
				}
			}
		}
		for (Double price : orderCumulative.keySet()){
			totalSells += orderCumulative.get(price).getRight();
			orderCumulative.replace(price, 
					new Pair<Integer, Integer>(orderCumulative.get(price).getLeft(), totalSells));
//			System.out.println("Price: " + price + " Sells: " + orderCumulative.get(price).getRight() +
//					 " Buys: " + orderCumulative.get(price).getLeft());
		}
		
	}

	private void addToNearestPrice(
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative,
			Order sellOrder, double sellPrice, Double nearestPrice) {
		if (nearestPrice != null) {
			Pair<Integer, Integer> cumulativePair = orderCumulative
					.remove(nearestPrice);
			orderCumulative.put(
					nearestPrice,
					new Pair<Integer, Integer>(cumulativePair
							.getLeft(), cumulativePair.getRight() + sellOrder.getSize()));
		} else {
			orderCumulative.put(sellPrice,
					new Pair<Integer, Integer>(0, sellOrder.getSize()));
		}
	}

	

	private void enterBuyCumulativeOrders(ArrayList<Order> buyOrderList,
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative) {
		int totalBuys = 0;
		for (Order buyOrder : buyOrderList) {
			setOriginalBuySize(orderCumulative, buyOrder);
		}
		if (orderCumulative.containsKey(0.0)) {
			setMarketOrder(orderCumulative);
		}
		NavigableMap<Double, Pair<Integer, Integer>> reverseOrder = orderCumulative.descendingMap();
		for (Double price : reverseOrder.keySet()){
			totalBuys = cumulateSize(orderCumulative, totalBuys, price);
		}	
	}

	private int cumulateSize(
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative,
			int totalBuys, Double price) {
		totalBuys += orderCumulative.get(price).getLeft();
		orderCumulative.replace(price, new Pair<Integer, Integer>(totalBuys,0));
		return totalBuys;
	}

	private void setMarketOrder(
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative) {
		Pair<Integer, Integer> cumulativePair = orderCumulative.remove(0.0);
		Double highestPrice = orderCumulative.lastKey();
		orderCumulative.put(getHighestPrice(highestPrice),
				new Pair<Integer, Integer>(cumulativePair.getLeft(), 0));
	}

	private Double getHighestPrice(Double d) {
		return (Double) (d + 1.00);
	}

	private void setOriginalBuySize(
			TreeMap<Double, Pair<Integer, Integer>> orderCumulative,
			Order buyOrder) {
		Pair<Integer, Integer> cumulativePair = new Pair<Integer, Integer>(buyOrder.getSize(), 0);
		double price = buyOrder.getPrice();
		if (orderCumulative.containsKey(price)){
			Pair<Integer, Integer> temp = orderCumulative.get(price);
			temp = new Pair<Integer, Integer>(temp.getLeft() + buyOrder.getSize(), 0);
			orderCumulative.replace(price, temp);
		}
		else{
			orderCumulative.put(price, cumulativePair);				
		}
	}
	
}
