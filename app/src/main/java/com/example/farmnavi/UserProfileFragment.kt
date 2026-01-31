package com.example.farmnavi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // UI references
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val profileName = view.findViewById<TextView>(R.id.profile_name)
        val profileRole = view.findViewById<TextView>(R.id.profile_role)
        val profileNameText = view.findViewById<TextView>(R.id.profile_name_text)
        val profileEmailText = view.findViewById<TextView>(R.id.profile_email_text)
        val profileLocationText = view.findViewById<TextView>(R.id.profile_location_text)
        val profilePhoneText = view.findViewById<TextView>(R.id.profile_phone_text)
        val logoutButton = view.findViewById<MaterialButton>(R.id.button_logout)

        // Edit button references
        val editName = view.findViewById<TextView>(R.id.edit_name)
        val editLocation = view.findViewById<TextView>(R.id.edit_location)
        val editEmail = view.findViewById<TextView>(R.id.edit_email)
        val editPhone = view.findViewById<TextView>(R.id.edit_phone)

        // Set up the toolbar
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // Fetch and display user data from Firestore
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = firestore.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val location = document.getString("location")
                        val phone = document.getString("phone")

                        profileName.text = name ?: "N/A"
                        profileNameText.text = name ?: "N/A"
                        profileEmailText.text = email ?: "N/A"
                        profileLocationText.text = location ?: "N/A"
                        profilePhoneText.text = phone ?: "N/A"

                    } else {
                        Log.d("UserProfileFragment", "No such document")
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("UserProfileFragment", "get failed with ", exception)
                    Toast.makeText(context, "Failed to load data.", Toast.LENGTH_SHORT).show()
                }
        } else {
            profileName.text = "Guest User"
        }

        // Set up click listeners for the Edit buttons
        editName.setOnClickListener {
            Toast.makeText(context, "Edit Name clicked", Toast.LENGTH_SHORT).show()
            // Here you can start a new activity or show an AlertDialog to edit the name.
        }

        editLocation.setOnClickListener {
            Toast.makeText(context, "Edit Location clicked", Toast.LENGTH_SHORT).show()
        }

        editEmail.setOnClickListener {
            Toast.makeText(context, "Edit Email clicked", Toast.LENGTH_SHORT).show()
        }

        editPhone.setOnClickListener {
            Toast.makeText(context, "Edit Phone clicked", Toast.LENGTH_SHORT).show()
        }

        // Set up the Logout button click listener
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Redirect the user to the SignUp_Activity
            val intent = Intent(activity, SignUp_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }
}
