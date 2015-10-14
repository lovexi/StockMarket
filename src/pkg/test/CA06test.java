package pkg.test;

import static org.junit.Assert.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import org.junit.Test;

import pkg.client.Pair;
import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.MarketHistory;
import pkg.market.api.IPO;
import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.OrderBook;
import pkg.order.OrderType;
import pkg.order.SellOrder;
import pkg.stock.Stock;
import pkg.trader.Trader;



public class CA06test {
	
	@Test
	public void testForMarket(){
		Stock stock1 = new Stock("SBUX", "Starbucks Corp.", 92.86);
		Stock stock2 = new Stock("HH", "Hello", -1.0);
		Market m = new Market("NASDAQ");
		try{
			m.addStock(stock1);
		} catch (StockMarketExpection e){
			e.printStackTrace();
		}
		try{
			m.addStock(stock2);
			fail("no correct exception");
		} catch (StockMarketExpection e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void stockTest() {
		Stock stock = new Stock("ASDF", "Abcdef", (float) 22.22);
		assertEquals(stock.getSymbol(), "ASDF");
		assertEquals((float) stock.getPrice(), (float) 22.22, 0);
		assertEquals(stock.getName(), "Abcdef");
		stock.setSymbol("STAR");
		assertEquals(stock.getSymbol(),"STAR");
		stock.setName("starbuck");
		assertEquals(stock.getName(),"starbuck");
		stock.setPrice(20.00);
		assertEquals((float) stock.getPrice(), (float) 20.00, 0);
	}
	
	@Test
	public void addStocks() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Stock stock1 = new Stock("SBUX", "Starbucks Corp.", 92.86);
		assertEquals(m.getStockForSymbol("SBUX").getName(), stock1.getName());
		assertEquals(m.getStockForSymbol("SBUX").getPrice(), stock1.getPrice(),0);
		assertEquals(m.getStockForSymbol("SBUX").getSymbol(), stock1.getSymbol());
	}
	
	@Test
	public void orderTest(){
		Trader trader1 = new Trader("Neda", 200000.00);
		BuyOrder buyo1 = new BuyOrder("ASDF", 100, 20.00, trader1);
		try{
			BuyOrder buyo2 = new BuyOrder("ASDF", 100, false, trader1);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void addStocksTwice(){
		Market m = new Market("NASDAQ");
		Stock stock1 = new Stock("ASDF", "Abcdef", (float) 22.22);
		
		try{
			stock1.setPrice(-1.0);
			m.addStock(stock1);
			fail("Can't get correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		stock1.setPrice(22.22);
		try{
			m.addStock(stock1);
			m.addStock(stock1);
			fail("Haven't got the correct StockMarketExpection");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void marketTest(){
		Market m = new Market("NASDAQ");
		
		try{
			m.removeStockFromStockList("HAHA");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		Stock stock1 = new Stock("ASDF", "Abcdef", (float) 22.22);
		try{
			m.addStock(stock1);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			m.updateStockPrice("HAHA", 20.00);
			fail("can't get a correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			m.updateStockPrice("ASDF", -20.00);
			fail("can't get a correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		m.printHistoryFor("ASDF");
		m.printStocks();
		
		MarketHistory marketHistory = new MarketHistory(m);
		m.setMarketHistory(marketHistory);
		assertEquals(m.getMarketHistory(), marketHistory);
	}
	
	@Test
	public void trderTest(){
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		
		Trader trader1 = new Trader("Neda", 200000.00);
		assertEquals(trader1.getCashInHand(),200000.00,0);
		
		Trader trader2 = new Trader("Scott", 100000.00);
		Trader trader15 = new Trader("T1", 300000.00);
		Trader trader16 = new Trader("T2", 300000.00);
		
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader2.buyFromBank(m, "SBUX", 300);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader1.buyFromBank(m,  "SBUX", 10000000);
			fail("Haven't got the correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
			trader15.placeNewMarketOrder(m, "SBUX", 1500, 0, OrderType.SELL);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader1.placeNewOrder(m, "SBUX", 10000, 97.0, OrderType.SELL);
			fail("Haven't got the correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader15.placeNewMarketOrder(m, "SBUX", 200 ,0 ,OrderType.SELL);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader2.placeNewOrder(m, "SBUX", 100, 101.0, OrderType.BUY);
			trader2.placeNewOrder(m, "SBUX", 100, 101.0, OrderType.BUY);
			trader16.placeNewMarketOrder(m, "SBUX", 700, 0, OrderType.BUY);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader2.placeNewOrder(m, "SBUX", 200, 101.0, OrderType.BUY);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader2.placeNewOrder(m, "SBUX", 200000, 101.0, OrderType.BUY);
			fail("Haven't got the correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader16.placeNewMarketOrder(m, "SBUX", 200, 101.0, OrderType.BUY);
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		try{
			trader16.placeNewMarketOrder(m, "SBUX", 2000000, 101.0, OrderType.BUY);
			fail("Haven't got the correct exception");
		}catch (StockMarketExpection e){
			e.printStackTrace();
		}
		
		m.triggerTrade();
		
		trader1.printTrader();		
	}
	
	@Test
	public void testForenterBuyCumulativeOrders()throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException{
		Trader trader1 = new Trader("Wo", 200000.00); 
		Trader trader2 = new Trader("Ai", 200000.00);
		Trader trader3 = new Trader("Huang", 200000.00);
		Trader trader4 = new Trader("Xi", 200000.00);
		Trader trader5 = new Trader("Yi", 200000.00);
		Trader trader6 = new Trader("Sheng", 200000.00);
		Trader trader7 = new Trader("Yi", 200000.00);
		Trader trader8 = new Trader("Shi", 300000.00);
		BuyOrder order1 = new BuyOrder("SBUX", 100, 97.0, trader1);
		BuyOrder order2 = new BuyOrder("SBUX", 200, 98.0, trader2);
		BuyOrder order3 = new BuyOrder("SBUX", 200, 97.5, trader3);
		SellOrder order4 = new SellOrder("SBUX", 300, 97.0, trader4);
		SellOrder order5 = new SellOrder("SBUX", 100, 98.0, trader5);
		SellOrder order6 = new SellOrder("SBUX", 200, 97.5, trader6);
		BuyOrder order7 = new BuyOrder("SBUX", 1000, 0.0, trader7);
		SellOrder order8 = new SellOrder("SBUX", 1000, 0.0, trader8);
		OrderBook book1 = new OrderBook(new Market("MARKET"));
		
		ArrayList<Order> buyOrderList = new ArrayList<Order>();
		Collections.addAll(buyOrderList, order1, order2, order3, order7);
		TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
		Method [] ma = OrderBook.class.getDeclaredMethods();
		for(Method m :ma){
			if(m.getName().equals("enterBuyCumulativeOrders")){
		    m.setAccessible(true);
		    Object [] prams  ={buyOrderList, orderCumulative};
		    m.invoke(book1,prams);
		    assertEquals((int)orderCumulative.get(97.0).getLeft(), 1500);
		    assertEquals((int)orderCumulative.get(97.5).getLeft(), 1400);
		    assertEquals((int)orderCumulative.get(98.0).getLeft(), 1200);
		    assertEquals((int)orderCumulative.get(99.0).getLeft(), 1000);
			}
		}
	}
	
	
	@Test
	public void testForDoubleSamePriceBuyOrdering()throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException{
		Trader trader1 = new Trader("Wo", 200000.00); 
		Trader trader2 = new Trader("Ai", 200000.00);
		Trader trader3 = new Trader("Huang", 200000.00);
		Trader trader4 = new Trader("Xi", 200000.00);
		Trader trader7 = new Trader("Yi", 200000.00);
		BuyOrder order1 = new BuyOrder("SBUX", 100, 97.0, trader1);
		BuyOrder order2 = new BuyOrder("SBUX", 200, 98.0, trader2);
		BuyOrder order3 = new BuyOrder("SBUX", 200, 97.5, trader3);
		BuyOrder order4 = new BuyOrder("SBUX", 200, 97.0, trader4);
		BuyOrder order7 = new BuyOrder("SBUX", 1000, 0.0, trader7);
		OrderBook book1 = new OrderBook(new Market("MARKET"));
		
		ArrayList<Order> buyOrderList = new ArrayList<Order>();
		Collections.addAll(buyOrderList, order1, order2, order3, order4, order7);
		TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
		Method [] ma = OrderBook.class.getDeclaredMethods();
		for(Method m :ma){
			if(m.getName().equals("enterBuyCumulativeOrders")){
		    m.setAccessible(true);
		    Object [] prams  ={buyOrderList, orderCumulative};
		    m.invoke(book1,prams);
		    assertEquals((int)orderCumulative.get(97.0).getLeft(), 1700);
		    assertEquals((int)orderCumulative.get(97.5).getLeft(), 1400);
		    assertEquals((int)orderCumulative.get(98.0).getLeft(), 1200);
		    assertEquals((int)orderCumulative.get(99.0).getLeft(), 1000);
			}
		}
	}
	
	@Test
	public void testForCalculateCumulative()throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException{
		Trader trader1 = new Trader("Wo", 200000.00); 
		Trader trader2 = new Trader("Ai", 200000.00);
		Trader trader3 = new Trader("Huang", 200000.00);
		Trader trader4 = new Trader("Xi", 200000.00);
		Trader trader5 = new Trader("Yi", 200000.00);
		Trader trader6 = new Trader("Sheng", 200000.00);
		Trader trader7 = new Trader("Yi", 200000.00);
		Trader trader8 = new Trader("Shi", 300000.00);
		BuyOrder order1 = new BuyOrder("SBUX", 100, 97.0, trader1);
		BuyOrder order2 = new BuyOrder("SBUX", 200, 98.0, trader2);
		BuyOrder order3 = new BuyOrder("SBUX", 200, 97.5, trader3);
		SellOrder order4 = new SellOrder("SBUX", 300, 97.0, trader4);
		SellOrder order5 = new SellOrder("SBUX", 100, 98.0, trader5);
		SellOrder order6 = new SellOrder("SBUX", 200, 97.5, trader6);
		BuyOrder order7 = new BuyOrder("SBUX", 1000, 0.0, trader7);
		SellOrder order8 = new SellOrder("SBUX", 1000, 0.0, trader8);
		OrderBook book1 = new OrderBook(new Market("MARKET"));
		
		ArrayList<Order> buyOrderList = new ArrayList<Order>();
		ArrayList<Order> sellOrderList  = new ArrayList<Order>();
		Collections.addAll(buyOrderList, order1, order2, order3, order7);
		Collections.addAll(sellOrderList, order4, order5, order6, order8);
		TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
		Method [] ma = OrderBook.class.getDeclaredMethods();
		for(Method m :ma){
			if(m.getName().equals("calculateCumulative")){
		    m.setAccessible(true);
		    Object [] prams  ={buyOrderList, sellOrderList, orderCumulative};
		    m.invoke(book1,prams);
		    assertEquals((int)orderCumulative.get(0.0).getRight(), 1000);
		    assertEquals((int)orderCumulative.get(97.0).getRight(), 1300);
		    assertEquals((int)orderCumulative.get(97.5).getRight(), 1500);
		    assertEquals((int)orderCumulative.get(98.0).getRight(), 1600);
		    // The price 99.0 is set for market Order. For sell order test, it will keep the same price from
		    // the nearest price due to the method ceilingkey().
		    assertEquals((int)orderCumulative.get(99.0).getRight(), 1600);
			}
		}	
	}
	
	@Test
	public void testForDoubleSameSellOrdering()throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException{
		Trader trader1 = new Trader("Wo", 200000.00); 
		Trader trader2 = new Trader("Ai", 200000.00);
		Trader trader3 = new Trader("Huang", 200000.00);
		Trader trader4 = new Trader("Xi", 200000.00);
		Trader trader5 = new Trader("Yi", 200000.00);
		Trader trader6 = new Trader("Sheng", 200000.00);
		Trader trader7 = new Trader("Yi", 200000.00);
		Trader trader8 = new Trader("Shi", 300000.00);
		BuyOrder order1 = new BuyOrder("SBUX", 100, 97.0, trader1);
		BuyOrder order2 = new BuyOrder("SBUX", 200, 98.0, trader2);
		BuyOrder order3 = new BuyOrder("SBUX", 200, 97.5, trader3);
		SellOrder order4 = new SellOrder("SBUX", 300, 97.0, trader4);
		SellOrder order5 = new SellOrder("SBUX", 100, 98.0, trader5);
		SellOrder order6 = new SellOrder("SBUX", 200, 97.5, trader6);
		BuyOrder order7 = new BuyOrder("SBUX", 1000, 0.0, trader7);
		SellOrder order8 = new SellOrder("SBUX", 1000, 0.0, trader8);
		OrderBook book1 = new OrderBook(new Market("MARKET"));
		
		Trader trader9 = new Trader("Shabi", 2000000.00);
		SellOrder order9 = new SellOrder("SBUX", 100, 98.0, trader9);
		
		ArrayList<Order> buyOrderList = new ArrayList<Order>();
		ArrayList<Order> sellOrderList  = new ArrayList<Order>();
		Collections.addAll(buyOrderList, order1, order2, order3, order7);
		Collections.addAll(sellOrderList, order4, order5, order6, order8, order9);
		TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
		Method [] ma = OrderBook.class.getDeclaredMethods();
		for(Method m :ma){
			if(m.getName().equals("calculateCumulative")){
		    m.setAccessible(true);
		    Object [] prams  ={buyOrderList, sellOrderList, orderCumulative};
		    m.invoke(book1,prams);
		    assertEquals((int)orderCumulative.get(0.0).getRight(), 1000);
		    assertEquals((int)orderCumulative.get(97.0).getRight(), 1300);
		    assertEquals((int)orderCumulative.get(97.5).getRight(), 1500);
		    assertEquals((int)orderCumulative.get(98.0).getRight(), 1700);
		    // The price 99.0 is set for market Order. For sell order test, it will keep the same price from
		    // the nearest price due to the method ceilingkey().
		    assertEquals((int)orderCumulative.get(99.0).getRight(), 1700);
			}
		}
		
	}
	
	@Test
	public void testForFindmatchprice()throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException{
		Trader trader1 = new Trader("Wo", 200000.00); 
		Trader trader2 = new Trader("Ai", 200000.00);
		Trader trader3 = new Trader("Huang", 200000.00);
		Trader trader4 = new Trader("Xi", 200000.00);
		Trader trader5 = new Trader("Yi", 200000.00);
		Trader trader6 = new Trader("Sheng", 200000.00);
		Trader trader7 = new Trader("Yi", 200000.00);
		Trader trader8 = new Trader("Shi", 300000.00);
		BuyOrder order1 = new BuyOrder("SBUX", 100, 97.0, trader1);
		BuyOrder order2 = new BuyOrder("SBUX", 200, 98.0, trader2);
		BuyOrder order3 = new BuyOrder("SBUX", 200, 97.5, trader3);
		SellOrder order4 = new SellOrder("SBUX", 300, 97.0, trader4);
		SellOrder order5 = new SellOrder("SBUX", 100, 98.0, trader5);
		SellOrder order6 = new SellOrder("SBUX", 200, 97.5, trader6);
		BuyOrder order7 = new BuyOrder("SBUX", 1000, 0.0, trader7);
		SellOrder order8 = new SellOrder("SBUX", 1000, 0.0, trader8);
		OrderBook book1 = new OrderBook(new Market("MARKET"));
		
		Trader trader9 = new Trader("Shabi", 2000000.00);
		SellOrder order9 = new SellOrder("SBUX", 100, 98.0, trader9);
		
		ArrayList<Order> buyOrderList = new ArrayList<Order>();
		ArrayList<Order> sellOrderList  = new ArrayList<Order>();
		Collections.addAll(buyOrderList, order1, order2, order3, order7);
		Collections.addAll(sellOrderList, order4, order5, order6, order8, order9);
		TreeMap<Double, Pair<Integer, Integer>> orderCumulative = new TreeMap<Double, Pair<Integer, Integer>>();
		Method [] ma = OrderBook.class.getDeclaredMethods();
		for(Method m : ma){
			if(m.getName().equals("calculateCumulative")){
				m.setAccessible(true);
				Object [] prams  ={buyOrderList, sellOrderList, orderCumulative};
			    m.invoke(book1,prams);
			}
		}
		for(Method m : ma){
			if(m.getName().equals("findMatchPrice")){
		    m.setAccessible(true);
		    Object prams  =orderCumulative;
		    double matchPrice = (Double)m.invoke(book1,prams);
		    assertEquals(matchPrice, 97.5, 0);
			}
		}
	}
	
	
}
