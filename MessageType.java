package blackjack;
/*
 *this enum contains all the messages that a client can initiate 
 */
public enum MessageType {
	HIT, STAND, DOUBLE_DOWN, START_ROUND,
	END_ROUND, LOGIN, LOGOUT,
	DEPOSIT, BET,
	UNKNOWN
}

