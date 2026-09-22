package com.herd.circle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herd.circle.databinding.ActivityMainBinding
import com.herd.circle.feed.HomeFeedFragment
import com.herd.circle.friends.FriendsFragment
import com.herd.circle.post.CreatePostActivity

/**
 * Hosts the main app's bottom-navigation flow (see Part 1 Design Document,
 * Section 4.8 — Navigation diagram): Home Feed and Friends are fragments
 * swapped in this container; Post opens CreatePostActivity directly since
 * it's a one-off action rather than a persistent tab.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(HomeFeedFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    showFragment(HomeFeedFragment())
                    true
                }
                R.id.nav_post -> {
                    startActivity(Intent(this, CreatePostActivity::class.java))
                    false // don't visually select this tab; it's an action, not a destination
                }
                R.id.nav_friends -> {
                    showFragment(FriendsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
