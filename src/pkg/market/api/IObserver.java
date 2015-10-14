package pkg.market.api;

public interface IObserver {
	public void update();

	// Abstract Method
	public void setSubject(ISubject subject);
}
