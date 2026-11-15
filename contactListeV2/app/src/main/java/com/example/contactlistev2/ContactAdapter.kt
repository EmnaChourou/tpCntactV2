package com.example.contactlistev2

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val contacts: List<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)

        val contact = getItem(position) ?: return view

        val imageViewContact = view.findViewById<ImageView>(R.id.imageViewContact)
        val textViewName = view.findViewById<TextView>(R.id.textViewName)
        val textViewPhone = view.findViewById<TextView>(R.id.textViewPhone)

        textViewName.text = "${contact.prenom} ${contact.nom}".trim()
        textViewPhone.text = contact.telephone

        loadContactImage(contact.imageUri, imageViewContact)

        return view
    }

    private fun loadContactImage(imageUriStr: String?, imageView: ImageView) {
        if (imageUriStr.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
            return
        }

        try {
            if (imageUriStr.startsWith("content://") || imageUriStr.startsWith("file://")) {
                val uri = Uri.parse(imageUriStr)
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        return
                    }
                }
                imageView.setImageURI(uri)
            } else if (imageUriStr.startsWith("drawable/")) {
                val drawableName = imageUriStr.substringAfter("drawable/")
                val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                if (resId != 0) {
                    imageView.setImageResource(resId)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
