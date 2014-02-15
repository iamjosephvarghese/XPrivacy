package biz.bokhorst.xprivacy;

public class RState {
	public boolean restricted;
	public boolean asked;
	public boolean partial = false;

	public static RState get(int uid, String restrictionName, String methodName) {
		RState state = new RState();

		// Get if on demand
		boolean onDemand = PrivacyManager.getSettingBool(0, PrivacyManager.cSettingOnDemand, true, false);
		if (onDemand)
			onDemand = PrivacyManager.getSettingBool(-uid, PrivacyManager.cSettingOnDemand, false, false);

		boolean allRestricted = true;
		boolean someRestricted = false;
		boolean asked = false;

		if (methodName == null) {
			// Examine the category/method states
			PRestriction query = PrivacyManager.getRestrictionEx(uid, restrictionName, null);
			someRestricted = query.restricted;
			for (PRestriction restriction : PrivacyManager.getRestrictionList(uid, restrictionName)) {
				allRestricted = (allRestricted && restriction.restricted);
				someRestricted = (someRestricted || restriction.restricted);
			}
			asked = query.asked;
		} else {
			// Examine the method state
			PRestriction query = PrivacyManager.getRestrictionEx(uid, restrictionName, methodName);
			allRestricted = query.restricted;
			someRestricted = false;
			asked = query.asked;
		}

		state.restricted = (allRestricted || someRestricted);
		state.partial = (!allRestricted && someRestricted);
		state.asked = (!onDemand || asked);

		return state;
	}

	public RState next() {
		RState next = new RState();
		next.restricted = !this.restricted;
		next.asked = true;
		return next;
	}
}
