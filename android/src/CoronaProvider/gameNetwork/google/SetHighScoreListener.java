//
//  LuaLoader.java
//  TemplateApp
//
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

// This corresponds to the name of the Lua library,
// e.g. [Lua] require "plugin.library"
package CoronaProvider.gameNetwork.google;

import CoronaProvider.gameNetwork.google.Listener;

import CoronaProvider.gameNetwork.google.GameHelper;

import com.naef.jnlua.LuaState;

import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeTaskDispatcher;
import com.ansca.corona.CoronaRuntimeTask;

import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;

public class SetHighScoreListener extends Listener implements OnScoreSubmittedListener{
	public SetHighScoreListener(CoronaRuntimeTaskDispatcher _dispatcher, int _listener) {
		super(_dispatcher, _listener);
	}

	@Override
	public void onScoreSubmitted(int statusCode, SubmitScoreResult scoreResult) {
		if (fListener < 0) {
			return;
		}

		final SubmitScoreResult finalScoreResult = scoreResult;
		CoronaRuntimeTask task = new CoronaRuntimeTask() {
			@Override
			public void executeUsing(CoronaRuntime runtime) {
				LuaState L = runtime.getLuaState();

				CoronaLua.newEvent(L, "setHighScore");

				L.pushString("setHighScore");
				L.setField(-2, TYPE);

				Listener.pushSubmitScoreResultToLua(L, finalScoreResult);
				L.setField(-2, DATA);

				try {
					CoronaLua.dispatchEvent(L, fListener, 0);
					CoronaLua.deleteRef(L, fListener);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		fDispatcher.send(task);
	}
}