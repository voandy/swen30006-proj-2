package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindParcelStrategy implements IDriveStrategy {

	@Override
	public boolean drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the left turn to get it
			autoctrl.turnLeft();
			autoctrl.currState = State.GET_PARCEL;
			return true;
		} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the right turn to get it
			autoctrl.turnRight();
			autoctrl.currState = State.GET_PARCEL;
			return true;
		}
		
		return false;
	}
}
