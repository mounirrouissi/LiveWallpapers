package moe.cyunrei.videolivewallpaper.activity.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import moe.cyunrei.videolivewallpaper.R

class FeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        val feedbackInput = view.findViewById<EditText>(R.id.feedback_input)
        val sendButton = view.findViewById<Button>(R.id.send_feedback_button)

        sendButton.setOnClickListener {
            val feedback = feedbackInput.text.toString()
            sendFeedbackEmail(feedback)
        }

        return view
    }

    private fun sendFeedbackEmail(feedback: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("mounirrouissi2@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback from App")
            putExtra(Intent.EXTRA_TEXT, feedback)
        }
        try {
            startActivity(Intent.createChooser(intent, "Send feedback..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}
