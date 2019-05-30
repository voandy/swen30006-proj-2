package mycontroller;

public class DriveStrategyFactory {
	private IDriveStrategy drivingStrategy = null;
	
	public IDriveStrategy getDriveStrategy(String strategyName) {
		switch (strategyName) {
		case "find-wall":
			drivingStrategy = new FindWallStrategy();
			break;
		case "find-parcel":
			drivingStrategy = new FindParcelStrategy();
			break;
		case "get-parcel":
			drivingStrategy = new GetParcelStrategy();
			break;
		case "find-finish":
			drivingStrategy = new FindFinishStrategy();
			break;
		}
		return drivingStrategy;
	}
}
