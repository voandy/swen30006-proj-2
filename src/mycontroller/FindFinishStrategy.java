package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindFinishStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
			// If there is a parcel to the left turn to get it
			autoctrl.turnLeft();
			autoctrl.currState = State.GO_STRAIGHT;
		} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
			// If there is a parcel to the right turn to get it
			autoctrl.turnRight();
			autoctrl.currState = State.GO_STRAIGHT;
		} else {
			// If wall no longer on left, turn left
			if(!autoctrl.checkFollowingWall(autoctrl.getOrientation(), currentView)) {
				autoctrl.turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(), currentView)) {
					autoctrl.turnRight();
				}
			}
		}
	}
}
