package com.devgardenaj.thisday.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.devgardenaj.thisday.infra.ThisDayApp
import com.devgardenaj.thisday.infra.ThisDayTheme
import com.devgardenaj.thisday.infra.dateToCustomDate
import com.devgardenaj.thisday.infra.dateToString
import com.devgardenaj.thisday.infra.localeChecker
import com.devgardenaj.thisday.widget.forceWidgetUpdate
import java.time.LocalDate
import androidx.core.graphics.toColorInt
import com.devgardenaj.thisday.R
import com.devgardenaj.thisday.view.TodayViewModel


@RequiresApi(Build.VERSION_CODES.O)
class TodayActivity : AppCompatActivity() {

    private val viewModel by lazy {
        val app = application as ThisDayApp
        val extras = intent.extras
        var asset = 0
        if (extras != null) {
            asset = intent.extras?.get("asset") as Int
        }
        val myDay = LocalDate.now().plusDays(asset.toLong())
        val today = dateToCustomDate(myDay)
        TodayViewModel(app.categoryRepo, app.infoRepo, today)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras
        var asset = 0
        if (extras != null) {
            asset = intent.extras?.get("asset") as Int
        }

        localeChecker(this)
        super.onCreate(savedInstanceState)
        setContent {
            ThisDayTheme {
                TodayScreen(viewModel, asset)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(viewModel: TodayViewModel, asset: Int) {
    val categories by viewModel.categories

    var newAsset = asset
    val context = LocalContext.current
    val dateForName = LocalDate.now().plusDays(asset.toLong())

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.loadInfo()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            newAsset--
                            val intent = Intent(context, TodayActivity::class.java).apply {
                                putExtra("asset", newAsset)
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous day",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = if (asset == 0) stringResource(R.string.today) else dateToString(dateForName),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (asset != 0) {
                            IconButton(onClick = {
                                newAsset++
                                val intent = Intent(context, TodayActivity::class.java).apply {
                                    putExtra("asset", newAsset)
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next day",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            )
        },
        bottomBar = { BottomPanel() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories, key = { it.categoryID }) { category ->
                CategoryRowCountable(
                    category = CategoryTemp(
                        category.categoryID,
                        category.categoryName,
                        category.categoryColor
                    ),
                    viewModel = viewModel,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryRowCountable(category: CategoryTemp, viewModel: TodayViewModel) {
    val infoList by viewModel.info
    val totalSum = infoList.sumOf { it.infoSum }
    val maxLimit = 24
    // remember(savedCount) — пересчитывается при каждом обновлении infoList из ViewModel,
    // но при нажатии +/- обновляется сразу (оптимистичный UI без мерцания)
    val savedCount = infoList.find { it.categoryID == category.categoryID }?.infoSum ?: 0
    var count by remember(savedCount) { mutableIntStateOf(savedCount) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = Color(category.categoryColor.toColorInt()),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = category.categoryName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalIconButton(
                onClick = {
                    if (count > 0) {
                        count--
                        viewModel.updateCount(category.categoryID, count)
                        forceWidgetUpdate(context)
                    }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = count.toString(),
                modifier = Modifier.widthIn(min = 28.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (totalSum < maxLimit) {
                FilledTonalIconButton(
                    onClick = {
                        count++
                        viewModel.updateCount(category.categoryID, count)
                        forceWidgetUpdate(context)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}
