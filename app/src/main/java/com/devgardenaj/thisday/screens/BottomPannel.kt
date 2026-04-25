package com.devgardenaj.thisday.screens


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devgardenaj.thisday.R
import com.devgardenaj.thisday.infra.ThisDayTheme

@Composable
fun BottomPanel() {
    val context = LocalContext.current

    ThisDayTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(bottom = 50.dp, top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavItem(
                    iconRes = R.drawable.icon_today,
                    contentDescription = "Today",
                    onClick = { context.startActivity(Intent(context, TodayActivity::class.java)) }
                )
                NavItem(
                    iconRes = R.drawable.icon_star,
                    contentDescription = "Categories",
                    onClick = { context.startActivity(Intent(context, CategoryActivity::class.java)) }
                )
                NavItem(
                    iconRes = R.drawable.icon_calendar,
                    contentDescription = "Graph",
                    onClick = { context.startActivity(Intent(context, GraphActivity::class.java)) }
                )
                NavItem(
                    iconRes = R.drawable.icon_setting,
                    contentDescription = "Settings",
                    onClick = { context.startActivity(Intent(context, SettingActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(iconRes: Int, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(26.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}
