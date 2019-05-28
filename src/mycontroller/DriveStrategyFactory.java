package mycontroller;

public class DriveStrategyFactory {
	private IDriveStrategy drivingStrategy = null;
	
	public IDriveStrategy getDriveStrategy(String strategyName) {
		switch (strategyName) {
		case "find-wall":
			drivingStrategy = new FindWallStrategy();
			break;
		case "follow-wall":
			drivingStrategy = new FollowWallStrategy();
			break;
		}
		return drivingStrategy;
	}
}
