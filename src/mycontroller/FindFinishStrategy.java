package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindFinishStrategy implements IDriveStrategy {

	@Override
	public boolean drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
			// If there is a finish tile to the left turn to get it
			autoctrl.turnLeft();
			autoctrl.currState = State.GO_STRAIGHT;
			return true;
		} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
			// If there is a finish tile to the right turn to get it
			autoctrl.turnRight();
			autoctrl.currState = State.GO_STRAIGHT;
			return true;
		}
		
		return false;
	}
}
