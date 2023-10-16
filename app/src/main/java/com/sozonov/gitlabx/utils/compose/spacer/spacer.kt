package com.sozonov.gitlabx.utils.compose.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSpacer(space: Int) = Spacer(modifier = Modifier.width(space.dp))

@Composable
fun VerticalSpacer(space: Int) = Spacer(modifier = Modifier.height(space.dp))
