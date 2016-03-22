/*******************************************************************************
 * Copyright (C) 2016 Business Factory, s.r.o.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package bf.io.openshop.ux;

import android.app.Activity;
import android.content.Intent;

import timber.log.Timber;

/**
 * Calling this activity cause restart application and new start from Splash activity.
 * It is used, when user change active/selected shop during lifetime.
 */
public class RestartAppActivity extends Activity {
    private static String TAG = RestartAppActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.tag(TAG);
        Timber.d("---------- onShopChange - finish old instances -----------");
        finish();
    }

    protected void onResume() {
        super.onResume();
        Timber.tag(TAG);
        Timber.d("---------- onShopChange starting new instance. -----------");
        startActivityForResult(new Intent(this, SplashActivity.class), 0);
    }
}
