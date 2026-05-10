package com.example.sijar.ui.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.sijar.R
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.UiState

@Composable
fun UiState.Error.asString(): String {
    return when (this.type) {
        is ErrorType.Network -> stringResource(R.string.error_no_internet)
        is ErrorType.Server -> stringResource(R.string.error_server)
        is ErrorType.Unauthorized -> stringResource(R.string.error_token_expired)
        is ErrorType.EmptyResponse -> stringResource(R.string.error_data_not_found)
        else -> this.message ?: stringResource(R.string.error_unknown)
    }
}
