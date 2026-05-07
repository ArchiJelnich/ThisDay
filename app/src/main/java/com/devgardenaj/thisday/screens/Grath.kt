package com.devgardenaj.thisday.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import com.devgardenaj.thisday.infra.ThisDayTheme
import com.devgardenaj.thisday.infra.localeChecker
import java.time.LocalDate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import com.devgardenaj.thisday.infra.averageCount
import com.devgardenaj.thisday.infra.CategoryColors
import com.devgardenaj.thisday.infra.DayInfo
import com.devgardenaj.thisday.infra.MonthAverage
import com.devgardenaj.thisday.infra.monthCount
import com.devgardenaj.thisday.infra.dateMYToString
import com.devgardenaj.thisday.infra.dateToCustomDate
import com.devgardenaj.thisday.infra.ThisDayApp
import androidx.core.graphics.toColorInt
import com.devgardenaj.thisday.R
import com.devgardenaj.thisday.room.Category
import com.devgardenaj.thisday.view.GraphViewModel

@RequiresApi(Build.VERSION_CODES.O)
class GraphActivity : ComponentActivity() {

    private val viewModel by lazy {
        val app = application as ThisDayApp
        val myDay = dateToCustomDate(LocalDate.now())
        val extras = intent.extras
        var asset = 0
        var assetMY = 0
        if (extras != null) {
            asset = intent.extras?.get("asset") as Int
        }
        if (extras != null) {
            assetMY = intent.extras?.get("assetMY") as Int
        }
        val newYear = myDay.year + asset
        val newMY = LocalDate.now().plusMonths(assetMY.toLong())
        GraphViewModel(app.categoryRepo, app.infoRepo, newYear, newMY)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras
        var asset = 0
        var assetMY = 0
        if (extras != null) {
            asset = intent.extras?.get("asset") as Int
        }
        if (extras != null) {
            assetMY = intent.extras?.get("assetMY") as Int
        }

        var selectedView = "month"
        if (extras != null) {
            selectedView = intent.extras?.get("view") as String
        }

        localeChecker(this)
        super.onCreate(savedInstanceState)
        setContent {
            ThisDayTheme {
                GraphScreen(viewModel, asset, assetMY, selectedView)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(viewModel: GraphViewModel, asset: Int, assetMY: Int, selectedViewExtra: String) {
    var selectedView by remember { mutableStateOf(selectedViewExtra) }
    var showFilterPicker by remember { mutableStateOf(false) }
    val categories by viewModel.categories
    var chosenCategory by remember {
        mutableStateOf(
            Category(
                categoryID = -1,
                categoryName = "null",
                categoryColor = "null",
                categoryDeletedFlag = 0,
            )
        )
    }
    var filterApplied by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("MyDebugs", "Stage 1")
        viewModel.loadCategories()
        viewModel.loadInfo(chosenCategory.categoryID)
    }
    averageCount(viewModel, chosenCategory)
    monthCount(viewModel, chosenCategory)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.graph),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
        bottomBar = { BottomPanel() }
    ) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedView == "month",
                onClick = { selectedView = "month" },
                label = { Text(stringResource(R.string.month)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilterChip(
                selected = selectedView == "year",
                onClick = { selectedView = "year" },
                label = { Text(stringResource(R.string.year)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )

            IconButton(onClick = { showFilterPicker = true }) {
                Icon(
                    painter = painterResource(R.drawable.icon_filter),
                    contentDescription = "filter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (filterApplied)
                            Modifier.background(
                                Color(chosenCategory.categoryColor.toColorInt()),
                                CircleShape
                            )
                        else
                            Modifier
                    )
            )
        }

        if (showFilterPicker) {
            Dialog(
                onDismissRequest = { showFilterPicker = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            filterApplied = false
                            showFilterPicker = false
                            chosenCategory = Category(
                                categoryID = -1,
                                categoryName = "null",
                                categoryColor = "null",
                                categoryDeletedFlag = 0,
                            )
                            Log.d("MyDebugs", "Stage 2")
                            viewModel.loadPerMInfo(chosenCategory.categoryID)
                            viewModel.loadInfo(chosenCategory.categoryID)
                        }
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 100.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {}
                    ) {
                        items(categories) { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        filterApplied = true
                                        showFilterPicker = false
                                        chosenCategory = category
                                        Log.d("MyDebugs", "Stage 3")
                                        viewModel.loadPerMInfo(chosenCategory.categoryID)
                                        viewModel.loadInfo(chosenCategory.categoryID)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(category.categoryColor.toColorInt()))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = category.categoryName,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            PeriodContent(selectedView, asset, assetMY, viewModel)
        }
    }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PeriodContent(view: String, asset: Int, assetMY: Int, viewModel: ViewModel) {
    var newAsset = asset
    var newAssetMY = assetMY
    val newDateMY = LocalDate.now().plusMonths(newAssetMY.toLong())
    val newDate = LocalDate.now().year + asset
    val context = LocalContext.current
    val dayOfm = newDateMY.lengthOfMonth()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (view == "year") {
                    newAsset--
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", newAsset)
                    intent.putExtra("assetMY", 0)
                    intent.putExtra("view", view)
                    context.startActivity(intent)
                }
                if (view == "month") {
                    newAssetMY--
                    Log.d("MyAsset", newAssetMY.toString())
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", 0)
                    intent.putExtra("assetMY", newAssetMY)
                    intent.putExtra("view", view)
                    context.startActivity(intent)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = when (view) {
                    "month" -> dateMYToString(newDateMY)
                    "year" -> newDate.toString()
                    else -> ""
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(onClick = {
                if (view == "year") {
                    newAsset++
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", newAsset)
                    intent.putExtra("assetMY", 0)
                    intent.putExtra("view", view)
                    context.startActivity(intent)
                }
                if (view == "month") {
                    newAssetMY++
                    Log.d("MyAsset", newAssetMY.toString())
                    val intent = Intent(context, GraphActivity::class.java)
                    intent.putExtra("asset", 0)
                    intent.putExtra("assetMY", newAssetMY)
                    intent.putExtra("view", view)
                    context.startActivity(intent)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PeriodGrid(view, viewModel as GraphViewModel, dayOfm)
    }
}

@Composable
fun PeriodGrid(
    view: String,
    viewModel: GraphViewModel,
    dayOfm: Int
) {
    Log.d("MyLog", "Look here!" + viewModel.categories)

    when (view) {
        "year" -> YearGrid(
            monthAverages = viewModel.monthAverages.value,
            categoryColors = viewModel.categoryColors
        )
        "month" -> MonthGrid(
            monthInfo = viewModel.monthInfo.value,
            categoryColors = viewModel.categoryColors,
            dayOfm = dayOfm
        )
    }
}

@Composable
fun MonthGrid(
    monthInfo: List<DayInfo>,
    categoryColors: Map<Int, Color>,
    dayOfm: Int
) {
    val blockSize = 17.dp
    val blockSpacing = 2.dp
    val maxBlocks = 24

    val blockHeight = blockSize + blockSpacing
    val graphHeight = blockHeight * maxBlocks

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyRow(
            verticalAlignment = Alignment.Bottom
        ) {
            items(monthInfo) { dayInfo ->
                val isToday = dayInfo.day == dayOfm

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .height(graphHeight)
                            .width(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val orderedData = remember(dayInfo, categoryColors) {
                                reorderByRainbow(dayInfo.sumsByCategory, categoryColors)
                            }

                            orderedData.forEach { (color, value) ->
                                repeat(value.coerceAtMost(maxBlocks)) {
                                    Box(
                                        modifier = Modifier
                                            .size(blockSize)
                                            .background(color, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(blockSpacing))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dayInfo.day.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}



@Composable
fun YearGrid(
    monthAverages: List<MonthAverage>,
    categoryColors: Map<Int, Color>
) {
    val blockSize = 17.dp
    val blockSpacing = 2.dp
    val maxBlocks = 24

    val blockHeight = blockSize + blockSpacing
    val graphHeight = blockHeight * maxBlocks

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyRow(
            verticalAlignment = Alignment.Bottom
        ) {
            items(monthAverages) { monthData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .height(graphHeight)
                            .width(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val orderedData = remember(monthData, categoryColors) {
                                reorderByRainbow(monthData.avgByCategory, categoryColors)
                            }

                            orderedData.forEach { (color, value) ->
                                repeat(value.coerceAtMost(maxBlocks)) {
                                    Box(
                                        modifier = Modifier
                                            .size(blockSize)
                                            .background(color, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(blockSpacing))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = monthData.month.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


fun reorderByRainbow(
    values: Map<Int, Int>,
    categoryColors: Map<Int, Color>
): List<Pair<Color, Int>> {
    val rainbowIndex = CategoryColors
        .withIndex()
        .associate { it.value to it.index }

    return values
        .mapNotNull { (categoryId, value) ->
            val color = categoryColors[categoryId] ?: return@mapNotNull null
            color to value
        }
        .sortedBy { (color, _) ->
            rainbowIndex[color] ?: Int.MAX_VALUE
        }
}
