import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.MainActivity

class OnboardingSlideFragment : Fragment() {

    companion object {
        private const val TOTAL_SLIDES = 3

        fun newInstance(position: Int): OnboardingSlideFragment {
            val fragment = OnboardingSlideFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val onboardingData = listOf(
            OnboardingSlide(R.drawable.dragon, "Title 1", "Description 1"),
            OnboardingSlide(R.drawable.dragon2, "Title 2", "Description 2"),
            OnboardingSlide(R.drawable.mickey, "Title 3", "Description 3")
        )

        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt("position") ?: 0
        val data = onboardingData[position]

        val imageView = view.findViewById<ImageView>(R.id.image)
        val titleView = view.findViewById<TextView>(R.id.title)
        val descriptionView = view.findViewById<TextView>(R.id.description)

        imageView.setImageResource(data.imageResId)
        titleView.text = data.title
        descriptionView.text = data.description

        val finishButton = view.findViewById<Button>(R.id.finishButton)

        if (position == TOTAL_SLIDES - 1) {
            finishButton.visibility = View.VISIBLE
            finishButton.setOnClickListener {
                // Handle the end of the onboarding process
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()

                // Set onboarding_completed to true
                val sharedPreferencesOnBoard = activity?.getSharedPreferences("onboarding_data", Context.MODE_PRIVATE)
                val editor = sharedPreferencesOnBoard?.edit()
                editor?.putBoolean("onboarding_completed", true)
                editor?.apply()
            }
        } else {
            finishButton.visibility = View.GONE
        }
    }

    data class OnboardingSlide(
        @DrawableRes val imageResId: Int,
        val title: String,
        val description: String
    )

}
