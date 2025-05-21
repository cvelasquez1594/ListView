package com.example.listview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listview.Hiring.ApiService
import com.example.listview.Hiring.Item
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            var items = getItems() // get list from json file
                .filter { it.name != null && it.name != "" } // filter values with name in blank or null
                .sortedWith(compareBy({ it.listId }, { it.name }))// sort by listId and name
            var filteredByGroup =
                items.groupBy({ it.listId }, { it.name }).toList() //group by listId
            setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Init(filteredByGroup)// initialize form
                }
            }
        }
    }
}

@Composable
fun Init(listId: List<Pair<Int, List<String?>>>) {
    LazyColumn(modifier = Modifier.padding(vertical = 6.dp)) {
        items(listId) {
            CardCreator(name = it.first.toString(), listId)
        }
    }
}

@Composable
fun CardCreator(name: String, listId: List<Pair<Int, List<String?>>>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ), modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        CardContent(name = name, listId)
    }
}

@Composable
fun CardContent(name: String, listId: List<Pair<Int, List<String?>>>) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "List ID: ")
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (expanded) {
                // print all names by corresponding ListId
                for (x in listId[name.toInt() - 1].second) {
                    ListItem(x.toString())
                }
            }
        }
    }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.ArrowDropDown,
            contentDescription =
                if (expanded) {
                    "show less"
                } else {
                    "show more"
                }
        )
    }
}


@Composable
fun ListItem(name: String) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    )
    {
        Text(
            text = "Name: $name",
            modifier = Modifier.padding(12.dp)
        )
    }

}

suspend fun getItems(): List<Item> {
    var retrofit = Retrofit.Builder()
        .baseUrl("https://hiring.fetch.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var service = retrofit.create<ApiService?>(ApiService::class.java)
    var call = service.getItems()
    return call
}
