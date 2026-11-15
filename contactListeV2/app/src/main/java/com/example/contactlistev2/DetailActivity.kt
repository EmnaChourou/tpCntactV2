package com.example.contactlistev2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class DetailActivity : AppCompatActivity() {

    private lateinit var imageViewDetailContact: ImageView
    private lateinit var editTextNom: TextInputEditText
    private lateinit var editTextPrenom: TextInputEditText
    private lateinit var editTextTelephone: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText

    private lateinit var buttonDeleteTopRight: Button
    private lateinit var buttonGallery: Button
    private lateinit var buttonCamera: Button
    private lateinit var buttonCall: Button
    private lateinit var buttonEmail: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private var currentContact: Contact? = null
    private var contactPosition: Int = -1
    private var selectedImageUriStr: String? = null

    private var cameraTempUri: Uri? = null

    private val pickGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            selectedImageUriStr = uri.toString()
            displayImage(selectedImageUriStr)
            Toast.makeText(this, "Photo sélectionnée", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Sélection de photo annulée", Toast.LENGTH_SHORT).show()
        }
    }

    private val takeCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && cameraTempUri != null) {
            selectedImageUriStr = cameraTempUri.toString()
            displayImage(selectedImageUriStr)
            Toast.makeText(this, "Photo capturée avec succès", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Prise de photo annulée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        retrieveIntentData()
        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                confirmAndDeleteContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        imageViewDetailContact = findViewById(R.id.imageViewDetailContact)
        editTextNom = findViewById(R.id.editTextNom)
        editTextPrenom = findViewById(R.id.editTextPrenom)
        editTextTelephone = findViewById(R.id.editTextTelephone)
        editTextEmail = findViewById(R.id.editTextEmail)

        buttonDeleteTopRight = findViewById(R.id.buttonDeleteTopRight)
        buttonGallery = findViewById(R.id.buttonGallery)
        buttonCamera = findViewById(R.id.buttonCamera)
        buttonCall = findViewById(R.id.buttonCall)
        buttonEmail = findViewById(R.id.buttonEmail)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)
    }

    private fun retrieveIntentData() {
        val intentData = intent ?: return
        currentContact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intentData.getParcelableExtra(MainActivity.EXTRA_CONTACT, Contact::class.java)
        } else {
            @Suppress("DEPRECATION")
            intentData.getParcelableExtra(MainActivity.EXTRA_CONTACT)
        }
        contactPosition = intentData.getIntExtra(MainActivity.EXTRA_POSITION, -1)

        currentContact?.let { contact ->
            editTextNom.setText(contact.nom)
            editTextPrenom.setText(contact.prenom)
            editTextTelephone.setText(contact.telephone)
            editTextEmail.setText(contact.email)
            selectedImageUriStr = contact.imageUri
            displayImage(selectedImageUriStr)
        }
    }

    private fun setupListeners() {
        buttonDeleteTopRight.setOnClickListener {
            confirmAndDeleteContact()
        }

        buttonGallery.setOnClickListener {
            pickGalleryImage.launch("image/*")
        }

        buttonCamera.setOnClickListener {
            launchCamera()
        }

        buttonCall.setOnClickListener {
            makePhoneCall()
        }

        buttonEmail.setOnClickListener {
            sendEmail()
        }

        buttonSave.setOnClickListener {
            saveContact()
        }

        buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun confirmAndDeleteContact() {
        val contactName = "${editTextPrenom.text} ${editTextNom.text}".trim()
        AlertDialog.Builder(this)
            .setTitle("Supprimer le contact")
            .setMessage("Voulez-vous vraiment supprimer le contact \"$contactName\" ?")
            .setPositiveButton("Supprimer") { _, _ ->
                val resultIntent = Intent().apply {
                    putExtra(MainActivity.EXTRA_IS_DELETED, true)
                    putExtra(MainActivity.EXTRA_POSITION, contactPosition)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun launchCamera() {
        try {
            val photoFile = File.createTempFile("contact_photo_", ".jpg", cacheDir)
            cameraTempUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                photoFile
            )
            cameraTempUri?.let { uri ->
                takeCameraImage.launch(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Impossible d'accéder à l'appareil photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall() {
        val phone = editTextTelephone.text?.toString()?.trim() ?: ""
        if (phone.isEmpty()) {
            editTextTelephone.error = "Numéro de téléphone vide"
            Toast.makeText(this, "Veuillez saisir un numéro de téléphone", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Aucune application disponible pour téléphoner", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val email = editTextEmail.text?.toString()?.trim() ?: ""
        if (email.isEmpty()) {
            editTextEmail.error = "Adresse e-mail vide"
            Toast.makeText(this, "Veuillez saisir une adresse e-mail", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Aucune application de messagerie disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContact() {
        val nom = editTextNom.text?.toString()?.trim() ?: ""
        val prenom = editTextPrenom.text?.toString()?.trim() ?: ""
        val phone = editTextTelephone.text?.toString()?.trim() ?: ""
        val email = editTextEmail.text?.toString()?.trim() ?: ""

        var isValid = true

        if (nom.isEmpty()) {
            editTextNom.error = "Le nom est obligatoire"
            isValid = false
        }
        if (prenom.isEmpty()) {
            editTextPrenom.error = "Le prénom est obligatoire"
            isValid = false
        }
        if (phone.isEmpty()) {
            editTextTelephone.error = "Le téléphone est obligatoire"
            isValid = false
        }
        if (email.isEmpty()) {
            editTextEmail.error = "L'e-mail est obligatoire"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        val contactToSave = currentContact ?: Contact(
            id = System.currentTimeMillis().toString(),
            nom = nom,
            prenom = prenom,
            telephone = phone,
            email = email,
            imageUri = selectedImageUriStr
        )

        contactToSave.nom = nom
        contactToSave.prenom = prenom
        contactToSave.telephone = phone
        contactToSave.email = email
        contactToSave.imageUri = selectedImageUriStr

        val resultIntent = Intent().apply {
            putExtra(MainActivity.EXTRA_CONTACT, contactToSave)
            putExtra(MainActivity.EXTRA_POSITION, contactPosition)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun displayImage(imageUriStr: String?) {
        if (imageUriStr.isNullOrEmpty()) {
            imageViewDetailContact.setImageResource(R.drawable.ic_launcher_foreground)
            return
        }

        try {
            if (imageUriStr.startsWith("content://") || imageUriStr.startsWith("file://")) {
                val uri = Uri.parse(imageUriStr)
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    if (bitmap != null) {
                        imageViewDetailContact.setImageBitmap(bitmap)
                        return
                    }
                }
                imageViewDetailContact.setImageURI(uri)
            } else if (imageUriStr.startsWith("drawable/")) {
                val drawableName = imageUriStr.substringAfter("drawable/")
                val resId = resources.getIdentifier(drawableName, "drawable", packageName)
                if (resId != 0) {
                    imageViewDetailContact.setImageResource(resId)
                } else {
                    imageViewDetailContact.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                imageViewDetailContact.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageViewDetailContact.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
