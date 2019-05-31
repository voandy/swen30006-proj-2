package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class FindParcelStrategy implements IDriveStrategy {

	@Override
	// Looks for parcels within view to the left and right of the car and directs the car to get them
	public boolean drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the left turn to get it and get new orientation after turning;
			WorldSpatial.Direction newOrientation = autoctrl.newOrientation(autoctrl.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
			autoctrl.turnLeft();

			if (autoctrl.checkParcelAhead(newOrientation, currentView)) {
				// if there is a parcel directly ahead after turning go into find wall mode
				autoctrl.currState = State.FIND_WALL;
				return true;
			} else {
				// otherwise go into get parcel mode
				autoctrl.currState = State.GET_PARCEL;
				return true;
			}
		} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the right turn to get it and get new orientation after turning;
			WorldSpatial.Direction newOrientation = autoctrl.newOrientation(autoctrl.getOrientation(), WorldSpatial.RelativeDirection.RIGHT);
			autoctrl.turnRight();

			if (autoctrl.checkParcelAhead(newOrientation, currentView)) {
				// if there is a parcel directly ahead after turning go into find wall mode
				autoctrl.currState = State.FIND_WALL;
				return true;
			} else {
				// otherwise go into get parcel mode
				autoctrl.currState = State.GET_PARCEL;
				return true;
			}
		}
		
		return false;
	}
}
