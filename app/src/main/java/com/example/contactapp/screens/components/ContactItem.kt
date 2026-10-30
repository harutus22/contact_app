package com.example.contactapp.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.contactapp.R
import com.example.contactapp.data_models.Contact
import com.example.contactapp.data_models.Organization

@Composable
fun ContactItem(
    contact: Contact,
    onItemClicked: () -> Unit
    ) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(4.dp)
            .clickable {
                onItemClicked()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = contact.photoUri ?: R.drawable.default_image,
            contentDescription = "",
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f)
                .padding(start = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = contact.name!!,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contact.mobilePhones[0],
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactItemPreview() {
    ContactItem(
        Contact(
            id = 1,
            name = "Jack",
            photoUri = null,
            mobilePhones = listOf("+145465561561"),
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
    ) {}
}