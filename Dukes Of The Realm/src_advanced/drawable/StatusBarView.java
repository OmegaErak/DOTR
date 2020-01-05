package drawable;

/**
 * The differents view of the status bars.
 */
public enum StatusBarView {
	/**
	 * Menu view by default.
	 */
	DefaultMenuView,

	/**
	 * Still in the menu, it should display the credits.
	 */
	CreditsView,

	/**
	 * Game view by default.
	 */
	DefaultGameView,

	/**
	 * View when the player clicks a castle.
	 */
	CastleView,

	/**
	 * View when recruiting troops.
	 */
	TroopsRecruitView,

	/**
	 * View when moving troops.
	 */
	TroopsMoveView,

	/**
	 * View when transfering money.
	 */
	MoneyTransferView
}
