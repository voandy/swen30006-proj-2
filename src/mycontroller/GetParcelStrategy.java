package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class GetParcelStrategy implements IDriveStrategy {

	@Override
	// goes straight until a parcel is picked up then finds wall
	public void drive(MyAutoController autoctrl) {
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkParcelAhead(autoctrl.getOrientation(), currentView)) {
			autoctrl.currState = State.FIND_WALL;
			return;
		}
	}

}
