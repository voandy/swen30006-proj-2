package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindParcelStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		Coordinate currentPosition = new Coordinate(autoctrl.getPosition());
		
		if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the left turn to get it
			autoctrl.turnLeft();
			autoctrl.currState = State.FIND_WALL;
		} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the right turn to get it
			autoctrl.turnRight();
			autoctrl.currState = State.FIND_WALL;
		} else {
			if (currentPosition.equals(autoctrl.foundWallCoord)) {
				// we've done a complete circle, turn right to find a new wall
				autoctrl.turnRight();
				autoctrl.currState = State.FIND_WALL;
			} else if(!autoctrl.checkFollowingWall(autoctrl.getOrientation(), currentView)) {
				// If wall no longer on left, turn left
				autoctrl.turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(), currentView)) {
					autoctrl.turnRight();
				}
			}
		}
		if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
			autoctrl.currState = State.FIND_FINISH;
		}
	}
}
