package com.example.contactlistev2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val contactList = ArrayList<Contact>()
    private lateinit var adapter: ContactAdapter
    private lateinit var listViewContacts: ListView

    private val detailActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data!!
                val isDeleted = data.getBooleanExtra(EXTRA_IS_DELETED, false)
                val position = data.getIntExtra(EXTRA_POSITION, -1)

                if (isDeleted) {
                    if (position in 0 until contactList.size) {
                        contactList.removeAt(position)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Contact supprimé avec succès", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val updatedContact: Contact? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data.getParcelableExtra(EXTRA_CONTACT, Contact::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        data.getParcelableExtra(EXTRA_CONTACT)
                    }

                    if (updatedContact != null && position in 0 until contactList.size) {
                        contactList[position] = updatedContact
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listViewContacts = findViewById(R.id.listViewContacts)

        initSampleContacts()

        adapter = ContactAdapter(this, contactList)
        listViewContacts.adapter = adapter

        listViewContacts.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = contactList[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(EXTRA_CONTACT, selectedContact)
                putExtra(EXTRA_POSITION, position)
            }
            detailActivityResultLauncher.launch(intent)
        }
    }

    private fun initSampleContacts() {
        if (contactList.isEmpty()) {
            contactList.add(
                Contact(
                    id = "1",
                    nom = "Ben Ali",
                    prenom = "Mohamed",
                    telephone = "21698123456",
                    email = "mohamed.benali@gmail.com",
                    imageUri = "drawable/ic_launcher_foreground"
                )
            )
            contactList.add(
                Contact(
                    id = "2",
                    nom = "Trabelsi",
                    prenom = "Sarra",
                    telephone = "21622345678",
                    email = "sarra.trabelsi@yahoo.fr",
                    imageUri = "drawable/ic_launcher_foreground"
                )
            )
            contactList.add(
                Contact(
                    id = "3",
                    nom = "Gharbi",
                    prenom = "Ahmed",
                    telephone = "21655987654",
                    email = "ahmed.gharbi@outlook.com",
                    imageUri = "drawable/ic_launcher_foreground"
                )
            )
        }
    }

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_IS_DELETED = "extra_is_deleted"
    }
}
