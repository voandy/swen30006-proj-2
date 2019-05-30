package mycontroller;

public interface IDriveStrategy {
	// drives car, returns true if the state has changed, otherwise returns false
	boolean drive(MyAutoController autoctrl);
}
