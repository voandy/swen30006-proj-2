package mycontroller;

import java.util.ArrayList;

public class CompositeDriveStrategy implements IDriveStrategy {
	
	private ArrayList<IDriveStrategy> driveStrategies = new ArrayList<IDriveStrategy>();
	
	public void add(IDriveStrategy driveStrategy) {
		driveStrategies.add(driveStrategy);
	}

	@Override
	public boolean drive(MyAutoController autoctrl) {
		// run all strategies until a state change occurs
		for (IDriveStrategy driveStrategy:driveStrategies) {
			if (driveStrategy.drive(autoctrl)) {
				return true;
			}
		}
		return false;
	}

}
