package bf.io.openshop.interfaces;

import bf.io.openshop.entities.User;

/**
 * Interface declaring methods for login dialog.
 */
public interface LoginDialogInterface {

    void successfulLoginOrRegistration(User user);

}
