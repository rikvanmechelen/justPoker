package edu.vub.at.commlib;

public enum PlayerState {
	Bet, Raise, Call, Fold, Check, AllIn, // From Client
	Unknown              // For Server: 'undecided'
, ReRaise
}
