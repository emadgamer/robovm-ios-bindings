
package org.robovm.bindings.facebook.manager;

import org.robovm.bindings.facebook.FBSession;
import org.robovm.bindings.facebook.FBSessionState;
import org.robovm.bindings.facebook.FBSessionStateHandler;
import org.robovm.bindings.facebook.manager.FacebookManager.ExtendPermissionsListener;
import org.robovm.bindings.facebook.manager.FacebookManager.LoginListener;
import org.robovm.bindings.facebook.manager.FacebookManager.LogoutListener;
import org.robovm.cocoatouch.foundation.NSError;

/** This class is used to handle common Facebook events. */
public class FBSessionStatusCallback implements FBSessionStateHandler {
	private static final String TAG = "[FBSessionStatusCallback] ";
	LoginListener loginListener = null;
	LogoutListener logoutListener = null;
	ExtendPermissionsListener extendPermissionsListener = null;

	@Override
	public void invoke (FBSession session, FBSessionState state, NSError error) {
		System.out.println(TAG + "Invoked...");
		if (error != null) {
			System.out.println(TAG + "Exception: " + error.description());

			if (error.toString().contains("Code=2")) {
				System.out.println(TAG + "User canceled login dialog.");
				if (session.getPermissions().size() == 0) {
					if (loginListener != null) loginListener.onNotAcceptingPermissions();
				}
			} else if (loginListener != null) {
				loginListener.onException(error);
			}
		}

		System.out.println(TAG + "FBSession: state=" + state.name() + ", session=" + session.toString());
		switch (state) {
		case Closed:
			logoutListener.onLogout();
			break;
		case ClosedLoginFailed:
			break;
		case Created:
			break;
		case CreatedTokenLoaded:
			break;
		case Opening:
			if (loginListener != null) loginListener.onRequest();
			break;
		case Open:
			if (extendPermissionsListener != null) {

			} else {
				System.out.println(FacebookManager.TAG + "Successfully logged in!");
				if (loginListener != null) loginListener.onLogin();
			}
			break;
		case OpenTokenExtended:
			if (extendPermissionsListener != null) {
				extendPermissionsListener.onSuccess();
				extendPermissionsListener = null;
			} else {
				System.out.println(FacebookManager.TAG + "Successfully logged in!");
				if (loginListener != null) loginListener.onLogin();
			}
			break;
		default:
			break;
		}
	}
}
