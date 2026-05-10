package com.example.sijar.ui.helper

import android.view.HapticFeedbackConstants
import android.view.View

object HapticHelper {

    fun performClick(view: View) {
        /** Simple Feedback **/
        view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP
        )
    }

    fun performSuccess(view: View) {
        /** Firm Feedback **/
        view.performHapticFeedback(
            HapticFeedbackConstants.CONFIRM
        )
    }

    fun performError(view: View) {
        /** "Wrong" Feedback **/
        view.performHapticFeedback(
            HapticFeedbackConstants.REJECT
        )
    }

    fun performLongPress(view: View) {
        /** Long Press **/
        view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS
        )
    }
}