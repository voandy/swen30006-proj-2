package mycontroller;

public class DriveStrategyFactory {
	private IDriveStrategy driveStrategy = null;
	private CompositeDriveStrategy compositeStrategy = null;
	
	public IDriveStrategy getDriveStrategy(String strategyName) {
		switch (strategyName) {
		case "find-wall":
			driveStrategy = new FindWallStrategy();
			break;
		case "find-parcel":
			compositeStrategy = new CompositeDriveStrategy();
			compositeStrategy.add(new FindParcelStrategy());
			compositeStrategy.add(new FollowWallStrategy());
			driveStrategy = compositeStrategy;
			break;
		case "get-parcel":
			driveStrategy = new GetParcelStrategy();
			break;
		case "find-finish":
			compositeStrategy = new CompositeDriveStrategy();
			compositeStrategy.add(new FindFinishStrategy());
			compositeStrategy.add(new FollowWallStrategy());
			driveStrategy = compositeStrategy;
			break;
		}
		return driveStrategy;
	}
}
