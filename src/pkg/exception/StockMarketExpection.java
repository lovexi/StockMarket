package pkg.exception;

public class StockMarketExpection extends Exception {

	
	private static final long serialVersionUID = 1L;
	private String str;

	public StockMarketExpection() {
		super();
	}

	public StockMarketExpection(String message) {
		super();
		this.str = message;
	}

	@Override
	public void printStackTrace() {
		System.out.println("Stock Market Violation Exception");
		System.out.println(str);
	}
}
