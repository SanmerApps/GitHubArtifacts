package dev.sanmer.github.artifacts.ui.screen.token.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import dev.sanmer.github.artifacts.R
import dev.sanmer.github.artifacts.ui.ktx.surface
import dev.sanmer.github.artifacts.ui.screen.token.EditTokenViewModel

@Composable
fun EditTokenItem(
    input: EditTokenViewModel.TokenInput,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(all = 15.dp)
        .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    DisposableEffect(input.isTokenChanged) {
        if (input.isTokenChanged) {
            input.updateCreatedAt()
        } else {
            input.revertCreatedAt()
        }
        onDispose {}
    }

    OutlinedTextField(
        state = input.name,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(R.string.edit_name)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        state = input.token,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(R.string.edit_token)) },
        modifier = Modifier.fillMaxWidth()
    )

    val day = stringResource(R.string.edit_day)
    val days = stringResource(R.string.edit_days)
    OutlinedTextField(
        state = input.lifetime,
        inputTransformation = InputTransformation.maxLength(3).then {
            if (!asCharSequence().isDigitsOnly()) {
                revertAllChanges()
            }
        },
        outputTransformation = {
            append(" ${if (length <= 1) day else days}")
            append(" (${input.expiredAt})")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        label = { Text(text = stringResource(R.string.edit_expiration)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier.fillMaxWidth()
    )
}