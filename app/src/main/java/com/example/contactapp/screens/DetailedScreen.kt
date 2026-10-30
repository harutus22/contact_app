package com.example.contactapp.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.contactapp.R
import com.example.contactapp.data_models.Contact
import com.example.contactapp.data_models.Organization

@Composable
fun DetailedScreen(
    contact: Contact,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 4.dp)
    ) {
        AsyncImage(
            model = contact.photoUri ?: R.drawable.default_image,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = contact.name!!,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Data(
            title = R.string.phone_numbers_title,
            mobile = contact.mobilePhones,
            home = contact.homePhones,
            work = contact.workPhones
        ) { number ->
            val dialUri = "tel:$number".toUri()
            val dialIntent = Intent(Intent.ACTION_DIAL, dialUri)

            try {
                context.startActivity(dialIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Data(
            title = R.string.emails_title,
            home = contact.homeEmails,
            work = contact.workEmails,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Data(
            title = R.string.addresses_title,
            home = contact.homeAddresses,
            work = contact.workAddresses,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LineText(title = R.string.organization_title, contact.organization?.company)
        Spacer(modifier = Modifier.height(8.dp))
        LineText(title = R.string.birthday, contact.birthday)
    }
}

@Composable
fun Data(
    title: Int,
    mobile: List<String>? = null,
    home: List<String>? = null,
    work: List<String>? = null,
    onNumberClicked: ((String) -> Unit)? = null
) {
    if (!mobile.isNullOrEmpty() || !home.isNullOrEmpty() || !work.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(title),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            TextDescription(R.string.mobile_subtitle, mobile, onNumberClicked = onNumberClicked)
            Spacer(modifier = Modifier.height(4.dp))
            TextDescription(R.string.home_subtitle, home, onNumberClicked = onNumberClicked)
            Spacer(modifier = Modifier.height(4.dp))
            TextDescription(R.string.work_subtitle, work, onNumberClicked = onNumberClicked)
        }
    }
}

@Composable
fun LineText(title: Int, name: String?) {
    if (!name.isNullOrEmpty())
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(title),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(name)
        }
}

@Composable
fun TextDescription(title: Int, list: List<String>?, onNumberClicked:((String) -> Unit)? = null) {
    if (!list.isNullOrEmpty()) {
        val text = list.joinToString()
        if (text.isNotBlank())
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(title))
                Spacer(Modifier.width(4.dp))
                list.forEach {
                    Text(it, modifier = Modifier.clickable(enabled = onNumberClicked != null){
                        onNumberClicked?.invoke(it)
                    })
                    if (it != list.last()){
                        Text(", ")
                    }
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedScreenPreview() {
    DetailedScreen(
        Contact(
            id = 1,
            name = "Jack",
            photoUri = null,
            mobilePhones = listOf("+145465561561", "+458484847474"),
            homePhones = listOf("+148484446"),
            workPhones = listOf("+14465446"),
            homeEmails = listOf("dasda@mail.ru"),
            workEmails = listOf("dasda@mail.ru"),
            homeAddresses = listOf("New York"),
            workAddresses = listOf("Washington"),
            organization = Organization(
                company = "Merkan",
                title = "sada",
                department = "Manddook"
            ),
            birthday = "22/13/2025"
        )
    )
}